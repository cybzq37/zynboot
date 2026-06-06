package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapFeature;
import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import com.zynboot.map.infrastructure.mapper.MapSpatialMapper;
import com.zynboot.map.service.MvtService;
import com.zynboot.map.service.datasource.FeatureService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;
import java.util.Map;

/**
 * 要素 CRUD + 空间查询 + 聚类 + 全文搜索。
 * 空间查询和搜索自动路由到 FILE / POSTGIS / ES。
 */
@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map")
public class FeatureController {

    private final MapFeatureMapper featureMapper;
    private final MapSpatialMapper spatialMapper;
    private final MvtService mvtService;
    private final FeatureService featureService;

    // ── 查询（自动路由）────────────────────────────────────

    @GetMapping("/layer/{layerId}/feature")
    public ApiResponse<?> listByLayer(
            @PathVariable String layerId,
            @RequestParam(required = false) String sourceId,
            @RequestParam(required = false) String bbox,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {

        int offset = (pageNum - 1) * pageSize;

        // bbox 空间查询：自动路由到 FILE / POSTGIS / ES
        if (bbox != null && !bbox.isBlank()) {
            String[] parts = bbox.split(",");
            if (parts.length == 4) {
                double[] bboxArr = new double[]{
                        Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3])};
                return ApiResponse.ok(featureService.queryByBbox(layerId, bboxArr, pageSize, offset));
            }
        }

        // 默认：查 map_feature（FILE 模式）
        LambdaQueryWrapper<MapFeature> wrapper = new LambdaQueryWrapper<MapFeature>()
                .eq(MapFeature::getLayerId, layerId)
                .eq(sourceId != null, MapFeature::getSourceId, sourceId)
                .last("LIMIT " + pageSize + " OFFSET " + offset);
        return ApiResponse.ok(featureMapper.selectList(wrapper));
    }

    @GetMapping("/layer/{layerId}/search")
    public ApiResponse<List<Map<String, Object>>> search(
            @PathVariable String layerId,
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return ApiResponse.ok(featureService.search(layerId, query, pageSize, offset));
    }

    // ── FILE 模式 CRUD ─────────────────────────────────────

    @GetMapping("/layer/{layerId}/feature/geojson")
    public ApiResponse<List<Map<String, Object>>> listAsGeoJson(
            @PathVariable String layerId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "100") int pageSize) {
        return ApiResponse.ok(spatialMapper.findAsGeoJson(layerId, pageSize, (pageNum - 1) * pageSize));
    }

    @GetMapping("/layer/{layerId}/feature/cluster")
    public ApiResponse<List<Map<String, Object>>> cluster(
            @PathVariable String layerId,
            @RequestParam(defaultValue = "10") int k,
            @RequestParam(required = false) String bbox) {
        if (bbox != null && !bbox.isBlank()) {
            String[] parts = bbox.split(",");
            if (parts.length == 4) {
                return ApiResponse.ok(spatialMapper.clusterWithBbox(layerId, k, parts[0], parts[1], parts[2], parts[3]));
            }
        }
        return ApiResponse.ok(spatialMapper.cluster(layerId, k));
    }

    @GetMapping("/feature/{id}")
    public ApiResponse<MapFeature> getById(@PathVariable Long id) {
        MapFeature feature = featureMapper.selectById(id);
        if (feature == null) throw BizException.notFound("要素");
        return ApiResponse.ok(feature);
    }

    @PostMapping("/layer/{layerId}/feature")
    public ApiResponse<Void> create(@PathVariable String layerId, @RequestBody MapFeature feature) {
        feature.setLayerId(layerId);
        featureMapper.insert(feature);
        mvtService.invalidateLayerCache(layerId);
        return ApiResponse.ok(null);
    }

    @PutMapping("/feature/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody MapFeature feature) {
        MapFeature existing = featureMapper.selectById(id);
        if (existing == null) throw BizException.notFound("要素");
        feature.setId(id);
        featureMapper.updateById(feature);
        mvtService.invalidateLayerCache(existing.getLayerId());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/feature/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        MapFeature existing = featureMapper.selectById(id);
        if (existing != null) {
            featureMapper.deleteById(id);
            mvtService.invalidateLayerCache(existing.getLayerId());
        }
        return ApiResponse.ok(null);
    }
}
