package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapFeature;
import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import com.zynboot.map.infrastructure.mapper.MapSpatialMapper;
import com.zynboot.map.service.MvtService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class FeatureController {

    private final MapFeatureMapper featureMapper;
    private final MapSpatialMapper spatialMapper;
    private final MvtService mvtService;

    // ── CRUD ───────────────────────────────────────────────

    @GetMapping("/layer/{layerId}/feature")
    public ApiResponse<List<MapFeature>> listByLayer(
            @PathVariable String layerId,
            @RequestParam(required = false) String sourceId,
            @RequestParam(required = false) String bbox,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {

        if (bbox != null && !bbox.isBlank()) {
            String[] parts = bbox.split(",");
            if (parts.length == 4) {
                return ApiResponse.ok(spatialMapper.findByBbox(
                        layerId, parts[0], parts[1], parts[2], parts[3], pageSize, (pageNum - 1) * pageSize));
            }
        }

        LambdaQueryWrapper<MapFeature> wrapper = new LambdaQueryWrapper<MapFeature>()
                .eq(MapFeature::getLayerId, layerId)
                .eq(sourceId != null, MapFeature::getSourceId, sourceId)
                .last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize));
        return ApiResponse.ok(featureMapper.selectList(wrapper));
    }

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
