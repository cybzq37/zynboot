package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapLayerVersion;
import com.zynboot.map.service.VersionService;
import com.zynboot.map.infrastructure.mapper.MapLayerVersionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class VersionController {

    private final MapLayerVersionMapper mapper;
    private final VersionService versionService;

    @GetMapping("/layer/{layerId}/version")
    public ApiResponse<List<MapLayerVersion>> list(@PathVariable String layerId) {
        return ApiResponse.ok(mapper.selectList(
                new LambdaQueryWrapper<MapLayerVersion>()
                        .eq(MapLayerVersion::getLayerId, layerId)
                        .orderByDesc(MapLayerVersion::getVersion)));
    }

    @GetMapping("/layer/{layerId}/version/{version}")
    public ApiResponse<MapLayerVersion> get(@PathVariable String layerId, @PathVariable Integer version) {
        MapLayerVersion v = mapper.selectOne(
                new LambdaQueryWrapper<MapLayerVersion>()
                        .eq(MapLayerVersion::getLayerId, layerId)
                        .eq(MapLayerVersion::getVersion, version));
        if (v == null) throw BizException.notFound("版本");
        return ApiResponse.ok(v);
    }

    @PostMapping("/layer/{layerId}/version")
    public ApiResponse<MapLayerVersion> create(@PathVariable String layerId) {
        return ApiResponse.ok(versionService.createSnapshot(layerId, "MANUAL", "手动快照"));
    }

    @PostMapping("/layer/{layerId}/rollback/{version}")
    public ApiResponse<Void> rollback(@PathVariable String layerId, @PathVariable Integer version) {
        versionService.rollback(layerId, version);
        return ApiResponse.ok(null);
    }
}
