package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.domain.aggregate.LayerAggregate;
import com.zynboot.map.domain.repository.LayerRepository;
import com.zynboot.map.infrastructure.entity.MapFeature;
import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class ExportController {

    private final LayerRepository layerRepository;
    private final MapFeatureMapper featureMapper;

    @GetMapping("/layer/{layerId}/export")
    public ApiResponse<List<MapFeature>> export(
            @PathVariable String layerId,
            @RequestParam(defaultValue = "geojson") String format,
            @RequestParam(required = false) String sourceId) {
        layerRepository.findById(layerId)
                .orElseThrow(() -> BizException.notFound("图层"));

        LambdaQueryWrapper<MapFeature> wrapper = new LambdaQueryWrapper<MapFeature>()
                .eq(MapFeature::getLayerId, layerId)
                .eq(sourceId != null, MapFeature::getSourceId, sourceId);

        return ApiResponse.ok(featureMapper.selectList(wrapper));
    }
}
