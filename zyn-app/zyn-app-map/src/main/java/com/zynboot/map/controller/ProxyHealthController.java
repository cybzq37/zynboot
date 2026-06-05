package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapSourceProxy;
import com.zynboot.map.infrastructure.mapper.MapSourceProxyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/source")
public class ProxyHealthController {

    private final MapSourceProxyMapper proxyMapper;

    @GetMapping("/{id}/proxy/health")
    public ApiResponse<MapSourceProxy> getHealth(@PathVariable String id) {
        MapSourceProxy proxy = proxyMapper.selectOne(
                new LambdaQueryWrapper<MapSourceProxy>().eq(MapSourceProxy::getSourceId, id));
        if (proxy == null) throw BizException.notFound("代理配置");
        return ApiResponse.ok(proxy);
    }

    @PostMapping("/{id}/proxy/check")
    public ApiResponse<Void> triggerCheck(@PathVariable String id) {
        MapSourceProxy proxy = proxyMapper.selectOne(
                new LambdaQueryWrapper<MapSourceProxy>().eq(MapSourceProxy::getSourceId, id));
        if (proxy == null) throw BizException.notFound("代理配置");
        // TODO: 触发健康检查任务
        return ApiResponse.ok(null);
    }
}
