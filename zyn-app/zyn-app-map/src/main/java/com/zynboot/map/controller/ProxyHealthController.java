package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapSourceProxy;
import com.zynboot.map.infrastructure.mapper.MapSourceProxyMapper;
import com.zynboot.map.service.ProxyHealthCheckService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.zynboot.infra.web.version.ApiVersion;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/source")
public class ProxyHealthController {

    private final MapSourceProxyMapper proxyMapper;
    private final ProxyHealthCheckService healthCheckService;

    @GetMapping("/{id}/proxy/health")
    public ApiResponse<MapSourceProxy> getHealth(@PathVariable String id) {
        MapSourceProxy proxy = proxyMapper.selectOne(
                new LambdaQueryWrapper<MapSourceProxy>().eq(MapSourceProxy::getSourceId, id));
        if (proxy == null) throw BizException.notFound("代理配置");
        return ApiResponse.ok(proxy);
    }

    @PostMapping("/{id}/proxy/check")
    public ApiResponse<MapSourceProxy> triggerCheck(@PathVariable String id) {
        MapSourceProxy proxy = proxyMapper.selectOne(
                new LambdaQueryWrapper<MapSourceProxy>().eq(MapSourceProxy::getSourceId, id));
        if (proxy == null) throw BizException.notFound("代理配置");
        healthCheckService.checkOne(proxy);
        return ApiResponse.ok(proxy);
    }
}
