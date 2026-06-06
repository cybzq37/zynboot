package com.zynboot.map.service.datasource;

import com.zynboot.map.infrastructure.entity.MapDataSource;
import com.zynboot.map.infrastructure.entity.MapLayerSource;
import com.zynboot.map.infrastructure.mapper.MapDataSourceMapper;
import com.zynboot.map.infrastructure.mapper.MapLayerSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 统一要素查询服务：按 source_type 自动路由到对应查询处理器。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureService {

    private final List<FeatureQueryHandler> handlers;
    private final MapLayerSourceMapper sourceMapper;
    private final MapDataSourceMapper dataSourceMapper;
    private final DynamicDataSourceService dynamicDataSourceService;

    /**
     * 按 bbox 查询图层的所有要素（自动路由 FILE / POSTGIS / ES）。
     */
    public List<Map<String, Object>> queryByBbox(String layerId, double[] bbox, int limit, int offset) {
        List<MapLayerSource> sources = sourceMapper.selectList(
                new LambdaQueryWrapper<MapLayerSource>()
                        .eq(MapLayerSource::getLayerId, layerId)
                        .eq(MapLayerSource::getStatus, "COMPLETED"));

        List<Map<String, Object>> allResults = new ArrayList<>();
        for (MapLayerSource source : sources) {
            FeatureQueryHandler handler = findHandler(source.getType());
            if (handler != null) {
                allResults.addAll(handler.queryByBbox(source.getId(), layerId, bbox, limit, offset));
            }
        }
        return allResults;
    }

    /**
     * 全文搜索（自动路由到 BM25 或 ES）。
     */
    public List<Map<String, Object>> search(String layerId, String query, int limit, int offset) {
        List<MapLayerSource> sources = sourceMapper.selectList(
                new LambdaQueryWrapper<MapLayerSource>()
                        .eq(MapLayerSource::getLayerId, layerId)
                        .eq(MapLayerSource::getStatus, "COMPLETED"));

        List<Map<String, Object>> allResults = new ArrayList<>();
        for (MapLayerSource source : sources) {
            FeatureQueryHandler handler = findHandler(source.getType());
            if (handler != null) {
                List<Map<String, Object>> results = handler.search(source.getId(), layerId, query, limit, offset);
                if (!results.isEmpty()) {
                    allResults.addAll(results);
                    break; // 找到支持搜索的 source 即返回
                }
            }
        }
        return allResults;
    }

    /**
     * 要素总数。
     */
    public long count(String layerId) {
        List<MapLayerSource> sources = sourceMapper.selectList(
                new LambdaQueryWrapper<MapLayerSource>()
                        .eq(MapLayerSource::getLayerId, layerId)
                        .eq(MapLayerSource::getStatus, "COMPLETED"));

        long total = 0;
        for (MapLayerSource source : sources) {
            FeatureQueryHandler handler = findHandler(source.getType());
            if (handler != null) {
                total += handler.count(source.getId(), layerId);
            }
        }
        return total;
    }

    /**
     * 测试外部数据源连接。
     */
    public boolean testDataSource(String dataSourceId) {
        MapDataSource ds = dataSourceMapper.selectById(dataSourceId);
        if (ds == null) return false;
        return dynamicDataSourceService.testConnection(ds);
    }

    private FeatureQueryHandler findHandler(String sourceType) {
        return handlers.stream()
                .filter(h -> h.supports(sourceType))
                .findFirst()
                .orElse(null);
    }
}
