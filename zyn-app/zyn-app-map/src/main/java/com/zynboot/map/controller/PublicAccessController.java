package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapInstance;
import com.zynboot.map.infrastructure.entity.MapInstanceLayer;
import com.zynboot.map.infrastructure.entity.MapPublish;
import com.zynboot.map.infrastructure.entity.MapLayerSource;
import com.zynboot.map.infrastructure.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开访问入口（无需登录）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/public")
public class PublicAccessController {

    private final MapPublishMapper publishMapper;
    private final MapInstanceMapper instanceMapper;
    private final MapInstanceLayerMapper instanceLayerMapper;
    private final MapLayerSourceMapper sourceMapper;

    @GetMapping("/{publishId}")
    public ApiResponse<MapInstance> getPublicMap(@PathVariable String publishId) {
        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            throw BizException.notFound("发布记录");
        }
        MapInstance instance = instanceMapper.selectById(pub.getInstanceId());
        if (instance == null) throw BizException.notFound("地图实例");
        return ApiResponse.ok(instance);
    }

    @GetMapping("/{publishId}/config")
    public ApiResponse<PublicMapConfig> getConfig(@PathVariable String publishId) {
        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            throw BizException.notFound("发布记录");
        }
        MapInstance instance = instanceMapper.selectById(pub.getInstanceId());
        if (instance == null) throw BizException.notFound("地图实例");

        List<MapInstanceLayer> layers = instanceLayerMapper.selectList(
                new LambdaQueryWrapper<MapInstanceLayer>()
                        .eq(MapInstanceLayer::getInstanceId, instance.getId())
                        .eq(MapInstanceLayer::getVisible, true)
                        .orderByAsc(MapInstanceLayer::getRenderOrder));

        return ApiResponse.ok(new PublicMapConfig(instance, layers));
    }

    @GetMapping("/{publishId}/tile/{sourceId}/{z}/{x}/{y}.png")
    public void getPublicTile(
            @PathVariable String publishId,
            @PathVariable String sourceId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        // 验证发布有效
        MapPublish pub = publishMapper.selectById(publishId);
        if (pub == null || !Boolean.TRUE.equals(pub.getIsActive())) {
            response.setStatus(404);
            return;
        }
        // 转发到 TileController
        // 简化实现：直接返回 200（实际应代理到 TileController）
        response.setStatus(200);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"public tile access ok\"}");
    }

    public record PublicMapConfig(MapInstance instance, List<MapInstanceLayer> layers) {}
}
