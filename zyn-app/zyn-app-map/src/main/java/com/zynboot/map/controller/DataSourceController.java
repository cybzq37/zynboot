package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapDataSource;
import com.zynboot.map.infrastructure.mapper.MapDataSourceMapper;
import com.zynboot.map.service.datasource.DynamicDataSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/datasource")
public class DataSourceController {

    private final MapDataSourceMapper mapper;
    private final DynamicDataSourceService dynamicDataSourceService;

    @GetMapping
    public ApiResponse<List<MapDataSource>> list() {
        List<MapDataSource> sources = mapper.selectList(null);
        // 隐藏密码字段
        sources.forEach(ds -> ds.setPassword(null));
        return ApiResponse.ok(sources);
    }

    @GetMapping("/{id}")
    public ApiResponse<MapDataSource> getById(@PathVariable String id) {
        MapDataSource ds = mapper.selectById(id);
        if (ds == null) throw BizException.notFound("数据源");
        ds.setPassword(null); // 不返回密码
        return ApiResponse.ok(ds);
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody MapDataSource ds) {
        mapper.insert(ds);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody MapDataSource ds) {
        ds.setId(id);
        mapper.updateById(ds);
        // 更新后刷新动态数据源
        dynamicDataSourceService.removeDataSource(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        dynamicDataSourceService.removeDataSource(id);
        mapper.deleteById(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/test")
    public ApiResponse<Map<String, Object>> testConnection(@PathVariable String id) {
        MapDataSource ds = mapper.selectById(id);
        if (ds == null) throw BizException.notFound("数据源");

        boolean ok = dynamicDataSourceService.testConnection(ds);
        if (ok) {
            return ApiResponse.ok(Map.of("status", "CONNECTED", "message", "连接成功"));
        } else {
            return ApiResponse.ok(Map.of("status", "FAILED", "message", "连接失败"));
        }
    }

    @PostMapping("/test")
    public ApiResponse<Map<String, Object>> testNewConnection(@RequestBody MapDataSource ds) {
        boolean ok = dynamicDataSourceService.testConnection(ds);
        if (ok) {
            return ApiResponse.ok(Map.of("status", "CONNECTED", "message", "连接成功"));
        } else {
            return ApiResponse.ok(Map.of("status", "FAILED", "message", "连接失败"));
        }
    }
}
