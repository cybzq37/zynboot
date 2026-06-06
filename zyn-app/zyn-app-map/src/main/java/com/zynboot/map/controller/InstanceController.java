package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapInstance;
import com.zynboot.map.infrastructure.entity.MapInstanceLayer;
import com.zynboot.map.infrastructure.entity.MapPublish;
import com.zynboot.map.infrastructure.mapper.MapInstanceMapper;
import com.zynboot.map.infrastructure.mapper.MapInstanceLayerMapper;
import com.zynboot.map.infrastructure.mapper.MapPublishMapper;
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
public class InstanceController {

    private final MapInstanceMapper instanceMapper;
    private final MapInstanceLayerMapper instanceLayerMapper;
    private final MapPublishMapper publishMapper;

    // ── Instance CRUD ──────────────────────────────────────

    @GetMapping("/instance")
    public ApiResponse<List<MapInstance>> listInstances() {
        return ApiResponse.ok(instanceMapper.selectList(null));
    }

    @GetMapping("/instance/{id}")
    public ApiResponse<MapInstance> getInstance(@PathVariable String id) {
        MapInstance instance = instanceMapper.selectById(id);
        if (instance == null) throw BizException.notFound("地图实例");
        return ApiResponse.ok(instance);
    }

    @PostMapping("/instance")
    public ApiResponse<Void> createInstance(@Valid @RequestBody MapInstance instance) {
        instanceMapper.insert(instance);
        return ApiResponse.ok(null);
    }

    @PutMapping("/instance/{id}")
    public ApiResponse<Void> updateInstance(@PathVariable String id, @Valid @RequestBody MapInstance instance) {
        instance.setId(id);
        instanceMapper.updateById(instance);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/instance/{id}")
    public ApiResponse<Void> deleteInstance(@PathVariable String id) {
        instanceLayerMapper.delete(
                new LambdaQueryWrapper<MapInstanceLayer>().eq(MapInstanceLayer::getInstanceId, id));
        publishMapper.delete(
                new LambdaQueryWrapper<MapPublish>().eq(MapPublish::getInstanceId, id));
        instanceMapper.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ── Instance Layer ─────────────────────────────────────

    @GetMapping("/instance/{id}/layers")
    public ApiResponse<List<MapInstanceLayer>> getLayers(@PathVariable String id) {
        return ApiResponse.ok(instanceLayerMapper.selectList(
                new LambdaQueryWrapper<MapInstanceLayer>()
                        .eq(MapInstanceLayer::getInstanceId, id)
                        .orderByAsc(MapInstanceLayer::getRenderOrder)));
    }

    @PutMapping("/instance/{id}/layers")
    public ApiResponse<Void> updateLayers(@PathVariable String id, @RequestBody List<MapInstanceLayer> layers) {
        // 整体替换
        instanceLayerMapper.delete(
                new LambdaQueryWrapper<MapInstanceLayer>().eq(MapInstanceLayer::getInstanceId, id));
        for (MapInstanceLayer layer : layers) {
            layer.setInstanceId(id);
            instanceLayerMapper.insert(layer);
        }
        return ApiResponse.ok(null);
    }

    // ── Publish ─────────────────────────────────────────────

    @GetMapping("/instance/{id}/publish")
    public ApiResponse<List<MapPublish>> listPublish(@PathVariable String id) {
        return ApiResponse.ok(publishMapper.selectList(
                new LambdaQueryWrapper<MapPublish>().eq(MapPublish::getInstanceId, id)));
    }

    @PostMapping("/instance/{id}/publish")
    public ApiResponse<MapPublish> publish(@PathVariable String id) {
        MapPublish pub = new MapPublish();
        pub.setInstanceId(id);
        pub.setType("PUBLIC");
        pub.setIsActive(true);
        publishMapper.insert(pub);
        return ApiResponse.ok(pub);
    }

    @DeleteMapping("/publish/{id}")
    public ApiResponse<Void> deletePublish(@PathVariable String id) {
        publishMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
