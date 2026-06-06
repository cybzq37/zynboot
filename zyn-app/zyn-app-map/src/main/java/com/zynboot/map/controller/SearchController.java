package com.zynboot.map.controller;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.mapper.MapSpatialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;
import java.util.Map;

/**
 * BM25 全文搜索控制器（基于 ParadeDB pg_search）。
 */
@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map")
public class SearchController {

    private final MapSpatialMapper spatialMapper;

    /**
     * BM25 全文搜索。
     *
     * @param layerId 图层 ID
     * @param query   搜索关键词
     * @param bbox    可选空间过滤（minx,miny,maxx,maxy）
     * @param pageNum 页码
     * @param pageSize 每页数量
     */
    @GetMapping("/layer/{layerId}/search")
    public ApiResponse<List<Map<String, Object>>> search(
            @PathVariable String layerId,
            @RequestParam String query,
            @RequestParam(required = false) String bbox,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {

        int offset = (pageNum - 1) * pageSize;

        if (bbox != null && !bbox.isBlank()) {
            String[] parts = bbox.split(",");
            if (parts.length == 4) {
                return ApiResponse.ok(spatialMapper.searchBm25WithBbox(
                        layerId, query, parts[0], parts[1], parts[2], parts[3], pageSize, offset));
            }
        }

        return ApiResponse.ok(spatialMapper.searchBm25(layerId, query, pageSize, offset));
    }
}
