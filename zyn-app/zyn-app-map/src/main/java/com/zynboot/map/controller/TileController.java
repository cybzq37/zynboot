package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.domain.aggregate.SourceAggregate;
import com.zynboot.map.domain.repository.LayerRepository;
import com.zynboot.map.domain.repository.SourceRepository;
import com.zynboot.map.infrastructure.entity.MapSourceProxy;
import com.zynboot.map.infrastructure.entity.MapSourceTile;
import com.zynboot.map.infrastructure.mapper.MapSourceProxyMapper;
import com.zynboot.map.infrastructure.mapper.MapSourceTileMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.infra.redis.RedisClient;
import com.zynboot.map.service.MvtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * ZXY 瓦片服务（栅格瓦片 + 矢量瓦片 MVT）。
 * <p>
 * 栅格瓦片：/{z}/{x}/{y}.png（本地 + 代理）
 * 矢量瓦片：/{z}/{x}/{y}.mvt（PostGIS ST_AsMVT 动态生成）
 * <p>
 * 缓存策略：Redis 热瓦片 + Nginx proxy_cache 磁盘缓存。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class TileController {

    private final SourceRepository sourceRepository;
    private final LayerRepository layerRepository;
    private final MapSourceTileMapper tileMapper;
    private final MapSourceProxyMapper proxyMapper;
    private final OkHttpClient httpClient;
    private final RedisClient redisClient;
    private final MvtService mvtService;

    @Value("${zyn.map.raster.root-path:./map-data/raster}")
    private String rasterRootPath;

    @Value("${zyn.map.tile.cache-enabled:true}")
    private boolean cacheEnabled;

    @Value("${zyn.map.tile.cache-ttl:3600}")
    private int cacheTtl;

    // ── 栅格瓦片 ───────────────────────────────────────────

    @GetMapping("/tile/{sourceId}/{z}/{x}/{y}.{format}")
    public void getTile(
            @PathVariable String sourceId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @PathVariable String format,
            @RequestParam(defaultValue = "3857") int srid,
            HttpServletResponse response) throws Exception {

        // MVT 走专用端点
        if ("mvt".equalsIgnoreCase(format) || "pbf".equalsIgnoreCase(format)) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Use /mvt/ endpoint for vector tiles\"}");
            return;
        }

        String cacheKey = "cache:tile:" + sourceId + ":" + z + ":" + x + ":" + y + ":" + srid;

        // 1. 查 Redis 缓存
        if (cacheEnabled) {
            byte[] cached = getCachedTile(cacheKey);
            if (cached != null) {
                writeRasterResponse(response, format, cached, null);
                return;
            }
        }

        // 2. 查 source 类型路由
        SourceAggregate source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> BizException.notFound("数据源"));

        byte[] tileBytes;
        if ("FILE".equals(source.getType())) {
            tileBytes = serveLocalTile(source, z, x, y, format);
        } else {
            tileBytes = proxyExternalTile(source, z, x, y, format);
        }

        if (tileBytes == null || tileBytes.length == 0) {
            response.setStatus(204);
            return;
        }

        // 3. 写入缓存
        if (cacheEnabled) {
            cacheTile(cacheKey, tileBytes);
        }

        // 4. 返回
        writeRasterResponse(response, format, tileBytes, null);
    }

    // ── 矢量瓦片（MVT）────────────────────────────────────

    /**
     * 矢量瓦片端点。
     * <p>
     * 路径：/mvt/{layerId}/{z}/{x}/{y}.mvt
     * 缓存：Redis 热瓦片 + Nginx 磁盘缓存（由 Cache-Control 控制）
     * 失效：图层数据变更时主动清 Redis，Nginx 通过 ETag 变化自动识别
     */
    @GetMapping("/mvt/{layerId}/{z}/{x}/{y}.pbf")
    public void getMvt(
            @PathVariable String layerId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(defaultValue = "3857") int srid,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // If-None-Match 头（浏览器/Nginx 缓存验证）
        String ifNoneMatch = request.getHeader("If-None-Match");

        MvtService.MvtResult result = mvtService.getMvt(layerId, z, x, y, srid);

        String etag = "\"" + (result.etag() != null ? result.etag() : "empty") + "\"";

        // 304 Not Modified（内容未变）
        if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            response.setStatus(304);
            return;
        }

        // 空瓦片
        if (result.data() == null || result.data().length == 0) {
            response.setStatus(204);
            response.setHeader("Cache-Control", "public, max-age=60");
            response.setHeader("ETag", etag);
            return;
        }

        // 写入响应
        writeMvtResponse(response, result.data(), etag);
    }

    /**
     * MVT 缓存失效端点（数据变更后调用）。
     */
    @PostMapping("/mvt/{layerId}/invalidate")
    public ApiResponse<Void> invalidateMvtCache(@PathVariable String layerId) {
        mvtService.invalidateLayerCache(layerId);
        return ApiResponse.ok(null);
    }

    // ── 瓦片状态 ───────────────────────────────────────────

    @GetMapping("/source/{id}/tile/status")
    public ApiResponse<MapSourceTile> tileStatus(@PathVariable String id) {
        MapSourceTile tile = tileMapper.selectOne(
                new LambdaQueryWrapper<MapSourceTile>().eq(MapSourceTile::getSourceId, id));
        if (tile == null) throw BizException.notFound("瓦片信息");
        return ApiResponse.ok(tile);
    }

    // ── 本地瓦片 ───────────────────────────────────────────

    private byte[] serveLocalTile(SourceAggregate source, int z, int x, int y, String format) throws Exception {
        MapSourceTile tile = tileMapper.selectOne(
                new LambdaQueryWrapper<MapSourceTile>().eq(MapSourceTile::getSourceId, source.getId()));
        if (tile == null || !"COMPLETED".equals(tile.getStatus())) return null;

        String tilePath = tile.getPath() != null ? tile.getPath() : source.getId() + "/tiles";
        Path filePath = Paths.get(rasterRootPath, source.getEntity().getLayerId(), tilePath,
                String.valueOf(z), String.valueOf(x), y + "." + format);

        if (!Files.exists(filePath)) return null;
        return Files.readAllBytes(filePath);
    }

    // ── 代理外部瓦片 ───────────────────────────────────────

    private byte[] proxyExternalTile(SourceAggregate source, int z, int x, int y, String format) {
        MapSourceProxy proxy = proxyMapper.selectOne(
                new LambdaQueryWrapper<MapSourceProxy>().eq(MapSourceProxy::getSourceId, source.getId()));
        if (proxy == null) return null;

        if ("DOWN".equals(proxy.getHealthStatus())) {
            log.warn("External service DOWN: sourceId={}", source.getId());
            return null;
        }

        String url = buildExternalUrl(proxy, z, x, y, format);
        if (url == null) return null;

        try {
            Request.Builder reqBuilder = new Request.Builder().url(url);
            if (proxy.getAuthType() != null && !"NONE".equals(proxy.getAuthType())) {
                attachAuth(reqBuilder, proxy);
            }

            Response resp = httpClient.newCall(reqBuilder.build()).execute();
            if (resp.isSuccessful() && resp.body() != null) {
                return resp.body().bytes();
            }
            log.warn("Proxy tile failed: sourceId={}, status={}", source.getId(), resp.code());
            return null;
        } catch (Exception e) {
            log.error("Proxy tile error: sourceId={}", source.getId(), e);
            proxy.setFailCount(proxy.getFailCount() + 1);
            proxyMapper.updateById(proxy);
            return null;
        }
    }

    private String buildExternalUrl(MapSourceProxy proxy, int z, int x, int y, String format) {
        String baseUrl = proxy.getUrl();
        if (baseUrl == null) return null;
        // 默认 XYZ 模式
        return baseUrl.replaceAll("/$", "") + "/" + z + "/" + x + "/" + y + "." + format;
    }

    private void attachAuth(Request.Builder builder, MapSourceProxy proxy) {
        switch (proxy.getAuthType()) {
            case "BASIC" -> {
                String encoded = java.util.Base64.getEncoder()
                        .encodeToString((proxy.getAuthValue() != null ? proxy.getAuthValue() : "").getBytes());
                builder.addHeader("Authorization", "Basic " + encoded);
            }
            case "TOKEN" -> builder.addHeader("Authorization", "Bearer " + proxy.getAuthValue());
            case "API_KEY" -> {
                String header = proxy.getAuthHeader() != null ? proxy.getAuthHeader() : "X-API-Key";
                builder.addHeader(header, proxy.getAuthValue());
            }
        }
    }

    // ── MBTiles 瓦片读取 ─────────────────────────────────────

    /**
     * 从 MBTiles 文件读取瓦片。
     * 路径：/mvt/mbtiles/{mbtilesPath}/{z}/{x}/{y}.pbf
     * mbtilesPath: MBTiles 文件路径（相对于 rasterRootPath）
     */
    @GetMapping("/mvt/mbtiles/{layerId}/{z}/{x}/{y}.pbf")
    public void getMvtFromMbtiles(
            @PathVariable String layerId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(defaultValue = "3857") int srid,
            HttpServletResponse response) throws Exception {

        // 查找 MBTiles 文件路径
        Path mbtilesPath = Paths.get(rasterRootPath, layerId, layerId + ".mbtiles");
        if (!Files.exists(mbtilesPath)) {
            response.setStatus(404);
            return;
        }

        // 从 SQLite MBTiles 读取瓦片
        // y 需要翻转: MBTiles 使用 TMS 坐标 (y = 2^z - 1 - y)
        int tmsY = (1 << z) - 1 - y;
        byte[] tileData = readMbtilesTile(mbtilesPath.toString(), z, x, tmsY);

        if (tileData == null || tileData.length == 0) {
            response.setStatus(204);
            return;
        }

        writeMvtResponse(response, tileData, "\"" + layerId + "-" + z + "-" + x + "-" + y + "\"");
    }

    private byte[] readMbtilesTile(String mbtilesPath, int z, int x, int y) {
        // 使用 JDBC 查询 SQLite
        try (var conn = java.sql.DriverManager.getConnection("jdbc:sqlite:" + mbtilesPath);
             var ps = conn.prepareStatement(
                     "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?")) {
            ps.setInt(1, z);
            ps.setInt(2, x);
            ps.setInt(3, y);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("tile_data");
                }
            }
        } catch (Exception e) {
            log.error("MBTiles read error: path={}, z={}, x={}, y={}", mbtilesPath, z, x, y, e);
        }
        return null;
    }

    // ── 响应写入 ───────────────────────────────────────────

    private void writeRasterResponse(HttpServletResponse response, String format, byte[] data, String etag) throws Exception {
        response.setContentType(getContentType(format));
        response.setHeader("Cache-Control", "public, max-age=300");
        if (etag != null) response.setHeader("ETag", etag);
        response.getOutputStream().write(data);
    }

    private void writeMvtResponse(HttpServletResponse response, byte[] data, String etag) throws Exception {
        response.setContentType("application/vnd.mapbox-vector-tile");
        response.setHeader("Cache-Control", "public, max-age=300");
        response.setHeader("ETag", etag);
        response.setHeader("Content-Length", String.valueOf(data.length));
        response.getOutputStream().write(data);
    }

    // ── 缓存工具 ───────────────────────────────────────────

    private byte[] getCachedTile(String key) {
        try {
            Object cached = redisClient.getObject(key);
            if (cached instanceof byte[] bytes) return bytes;
            if (cached instanceof String str) return str.getBytes();
        } catch (Exception ignored) {}
        return null;
    }

    private void cacheTile(String key, byte[] data) {
        try {
            redisClient.putObject(key, data, Duration.ofSeconds(cacheTtl));
        } catch (Exception ignored) {}
    }

    private String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "webp" -> "image/webp";
            default -> "image/png";
        };
    }
}
