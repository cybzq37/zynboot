package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapDataSource;
import com.zynboot.map.infrastructure.mapper.MapDataSourceMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/datasource")
public class DataSourceController {

    private final MapDataSourceMapper mapper;

    @GetMapping
    public ApiResponse<List<MapDataSource>> list() {
        return ApiResponse.ok(mapper.selectList(null));
    }

    @GetMapping("/{id}")
    public ApiResponse<MapDataSource> getById(@PathVariable String id) {
        MapDataSource ds = mapper.selectById(id);
        if (ds == null) throw BizException.notFound("数据源");
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
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        mapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
