package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapLayerStyle;
import com.zynboot.map.infrastructure.mapper.MapLayerStyleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map")
public class StyleController {

    private final MapLayerStyleMapper mapper;

    @GetMapping("/layer/{layerId}/style")
    public ApiResponse<List<MapLayerStyle>> listByLayer(@PathVariable String layerId) {
        return ApiResponse.ok(mapper.selectList(
                new LambdaQueryWrapper<MapLayerStyle>().eq(MapLayerStyle::getLayerId, layerId)));
    }

    @PostMapping("/layer/{layerId}/style")
    public ApiResponse<Void> create(@PathVariable String layerId, @Valid @RequestBody MapLayerStyle style) {
        style.setLayerId(layerId);
        mapper.insert(style);
        return ApiResponse.ok(null);
    }

    @PutMapping("/style/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody MapLayerStyle style) {
        style.setId(id);
        mapper.updateById(style);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/style/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        mapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
