package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapBasemap;
import com.zynboot.map.infrastructure.mapper.MapBasemapMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/basemap")
public class BasemapController {

    private final MapBasemapMapper mapper;

    @GetMapping
    public ApiResponse<List<MapBasemap>> list() {
        return ApiResponse.ok(mapper.selectList(
                new LambdaQueryWrapper<MapBasemap>().orderByAsc(MapBasemap::getSortOrder)));
    }

    @GetMapping("/default")
    public ApiResponse<MapBasemap> getDefault() {
        MapBasemap basemap = mapper.selectOne(
                new LambdaQueryWrapper<MapBasemap>().eq(MapBasemap::getIsDefault, true));
        if (basemap == null) throw BizException.notFound("默认底图");
        return ApiResponse.ok(basemap);
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody MapBasemap basemap) {
        mapper.insert(basemap);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody MapBasemap basemap) {
        basemap.setId(id);
        mapper.updateById(basemap);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        mapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
