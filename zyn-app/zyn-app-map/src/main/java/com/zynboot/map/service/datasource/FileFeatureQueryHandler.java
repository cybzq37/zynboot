package com.zynboot.map.service.datasource;

import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import com.zynboot.map.infrastructure.mapper.MapSpatialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * FILE 模式查询：查 map_feature 表（PostGIS）。
 */
@Component
@RequiredArgsConstructor
public class FileFeatureQueryHandler implements FeatureQueryHandler {

    private final MapFeatureMapper featureMapper;
    private final MapSpatialMapper spatialMapper;

    @Override
    public boolean supports(String sourceType) {
        return "FILE".equals(sourceType);
    }

    @Override
    public List<Map<String, Object>> queryByBbox(String sourceId, String layerId,
                                                  double[] bbox, int limit, int offset) {
        return spatialMapper.findAsGeoJson(layerId, limit, offset);
    }

    @Override
    public List<Map<String, Object>> search(String sourceId, String layerId,
                                             String query, int limit, int offset) {
        return spatialMapper.searchBm25(layerId, query, limit, offset);
    }

    @Override
    public long count(String sourceId, String layerId) {
        return featureMapper.countByLayerId(layerId);
    }

    @Override
    public long countByBbox(String sourceId, String layerId, double[] bbox) {
        // 简化：返回总数（精确 bbox count 需要额外 SQL）
        return featureMapper.countByLayerId(layerId);
    }
}
