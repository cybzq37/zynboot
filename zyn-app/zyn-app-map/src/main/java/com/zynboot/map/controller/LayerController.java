package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.command.layer.LayerSaveCmd;
import com.zynboot.map.domain.aggregate.LayerAggregate;
import com.zynboot.map.domain.repository.LayerRepository;
import com.zynboot.map.handler.query.LayerQueryHandler;
import com.zynboot.map.response.layer.LayerRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/layer")
public class LayerController {

    private final LayerRepository layerRepository;
    private final LayerQueryHandler layerQueryHandler;

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
    public ApiResponse<Void> delete(@PathVariable String id) {
        layerRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
