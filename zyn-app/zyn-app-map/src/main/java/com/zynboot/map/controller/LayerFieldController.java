package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapLayerField;
import com.zynboot.map.infrastructure.mapper.MapLayerFieldMapper;
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
public class LayerFieldController {

    private final MapLayerFieldMapper mapper;

    @GetMapping("/layer/{layerId}/field")
    public ApiResponse<List<MapLayerField>> list(@PathVariable String layerId) {
        return ApiResponse.ok(mapper.selectList(
                new LambdaQueryWrapper<MapLayerField>()
                        .eq(MapLayerField::getLayerId, layerId)
                        .orderByAsc(MapLayerField::getSortOrder)));
    }

    @PostMapping("/layer/{layerId}/field")
    public ApiResponse<Void> create(@PathVariable String layerId, @Valid @RequestBody MapLayerField field) {
        field.setLayerId(layerId);
        mapper.insert(field);
        return ApiResponse.ok(null);
    }

    @PutMapping("/field/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody MapLayerField field) {
        field.setId(id);
        mapper.updateById(field);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/field/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        mapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
