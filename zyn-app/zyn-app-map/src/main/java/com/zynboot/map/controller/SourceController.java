package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.domain.aggregate.SourceAggregate;
import com.zynboot.map.domain.repository.SourceRepository;
import com.zynboot.map.infrastructure.entity.MapLayerSource;
import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map")
public class SourceController {

    private final SourceRepository sourceRepository;
    private final MapFeatureMapper featureMapper;

    @GetMapping("/layer/{layerId}/source")
    public ApiResponse<List<SourceAggregate>> listByLayer(@PathVariable String layerId) {
        return ApiResponse.ok(sourceRepository.findByLayerId(layerId));
    }

    @GetMapping("/source/{id}")
    public ApiResponse<MapLayerSource> getById(@PathVariable String id) {
        SourceAggregate source = sourceRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("数据源"));
        return ApiResponse.ok(source.getEntity());
    }

    @DeleteMapping("/source/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        sourceRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
