package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapInstance;
import com.zynboot.map.infrastructure.entity.MapInstanceLayer;
import com.zynboot.map.infrastructure.entity.MapPublish;
import com.zynboot.map.infrastructure.entity.MapLayerSource;
import com.zynboot.map.infrastructure.entity.MapSourceTile;
import com.zynboot.map.infrastructure.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.infra.redis.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/public")
public class PublicAccessController {

    private final MapPublishMapper publishMapper;
    private final MapInstanceMapper instanceMapper;
    private final MapInstanceLayerMapper instanceLayerMapper;
    private final MapLayerSourceMapper sourceMapper;
    private final MapSourceTileMapper tileMapper;
    private final RedisClient redisClient;

    @Value("${zyn.map.raster.root-path:./map-data/raster}")
    private String rasterRootPath;

    @GetMapping("/{publishId}")
    public ApiResponse<MapInstance> getPublicMap(@PathVariable String publishId) {
        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            throw BizException.notFound("发布记录");
        }
        MapInstance instance = instanceMapper.selectById(pub.getInstanceId());
        if (instance == null) throw BizException.notFound("地图实例");
        return ApiResponse.ok(instance);
    }

    @GetMapping("/{publishId}/config")
    public ApiResponse<PublicMapConfig> getConfig(@PathVariable String publishId) {
        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            throw BizException.notFound("发布记录");
        }
        MapInstance instance = instanceMapper.selectById(pub.getInstanceId());
        if (instance == null) throw BizException.notFound("地图实例");

        List<MapInstanceLayer> layers = instanceLayerMapper.selectList(
                new LambdaQueryWrapper<MapInstanceLayer>()
                        .eq(MapInstanceLayer::getInstanceId, instance.getId())
                        .eq(MapInstanceLayer::getVisible, true)
                        .orderByAsc(MapInstanceLayer::getRenderOrder));

        return ApiResponse.ok(new PublicMapConfig(instance, layers));
    }

    @GetMapping("/{publishId}/tile/{sourceId}/{z}/{x}/{y}.png")
    public void getPublicTile(
            @PathVariable String publishId,
            @PathVariable String sourceId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {

        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            response.setStatus(404);
            return;
        }

        // 查找 source 并读取本地瓦片
        MapLayerSource source = sourceMapper.selectById(sourceId);
        if (source == null) {
            response.setStatus(404);
            return;
        }

        MapSourceTile tile = tileMapper.selectOne(
                new LambdaQueryWrapper<MapSourceTile>().eq(MapSourceTile::getSourceId, sourceId));
        if (tile == null || !"COMPLETED".equals(tile.getStatus())) {
            response.setStatus(204);
            return;
        }

        String tilePath = tile.getPath() != null ? tile.getPath() : sourceId + "/tiles";
        Path filePath = Paths.get(rasterRootPath, source.getLayerId(), tilePath,
                String.valueOf(z), String.valueOf(x), y + ".png");

        if (!Files.exists(filePath)) {
            response.setStatus(204);
            return;
        }

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "public, max-age=300");
        Files.copy(filePath, response.getOutputStream());
    }

    public record PublicMapConfig(MapInstance instance, List<MapInstanceLayer> layers) {}
}
