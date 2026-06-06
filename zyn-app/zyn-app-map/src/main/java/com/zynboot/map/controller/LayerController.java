package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.command.layer.LayerSaveCmd;
import com.zynboot.map.domain.aggregate.LayerAggregate;
import com.zynboot.map.domain.repository.LayerRepository;
import com.zynboot.map.domain.repository.SourceRepository;
import com.zynboot.map.handler.query.LayerQueryHandler;
import com.zynboot.map.infrastructure.entity.MapLayerField;
import com.zynboot.map.infrastructure.entity.MapLayerStyle;
import com.zynboot.map.infrastructure.entity.MapLayerVersion;
import com.zynboot.map.response.layer.LayerRes;
import com.zynboot.map.infrastructure.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/layer")
public class LayerController {

    private final LayerRepository layerRepository;
    private final SourceRepository sourceRepository;
    private final LayerQueryHandler layerQueryHandler;
    private final MapLayerFieldMapper fieldMapper;
    private final MapLayerStyleMapper styleMapper;
    private final MapLayerVersionMapper versionMapper;

    @GetMapping
    public ApiResponse<List<LayerRes>> list(@RequestParam(required = false) String groupId) {
        List<LayerRes> layers = layerRepository.findByGroupId(groupId).stream()
                .map(layerQueryHandler::toRes)
                .toList();
        return ApiResponse.ok(layers);
    }

    @GetMapping("/{id}")
    public ApiResponse<LayerRes> getById(@PathVariable String id) {
        LayerAggregate layer = layerRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("图层"));
        return ApiResponse.ok(layerQueryHandler.toRes(layer));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody LayerSaveCmd cmd) {
        LayerAggregate layer = LayerAggregate.create(cmd.getGroupId(), cmd.getName(), cmd.getType(), cmd.getTargetSrid());
        layer.updateInfo(cmd.getName(), cmd.getTitle(), cmd.getDescription(), cmd.getRenderOrder(),
                null, null, null, cmd.getMinZoom(), cmd.getMaxZoom(), cmd.getOpacity());
        layerRepository.save(layer);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody LayerSaveCmd cmd) {
        LayerAggregate layer = layerRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("图层"));
        layer.updateInfo(cmd.getName(), cmd.getTitle(), cmd.getDescription(), cmd.getRenderOrder(),
                null, null, null, cmd.getMinZoom(), cmd.getMaxZoom(), cmd.getOpacity());
        layerRepository.update(layer);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> delete(@PathVariable String id) {
        layerRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("图层"));

        // 级联删除：source → field → style → version
        sourceRepository.findByLayerId(id).forEach(s -> sourceRepository.delete(s.getId()));
        fieldMapper.delete(new LambdaQueryWrapper<MapLayerField>().eq(MapLayerField::getLayerId, id));
        styleMapper.delete(new LambdaQueryWrapper<MapLayerStyle>().eq(MapLayerStyle::getLayerId, id));
        versionMapper.delete(new LambdaQueryWrapper<MapLayerVersion>().eq(MapLayerVersion::getLayerId, id));
        layerRepository.delete(id);

        return ApiResponse.ok(null);
    }
}
