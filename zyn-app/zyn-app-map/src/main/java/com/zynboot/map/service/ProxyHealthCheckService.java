package com.zynboot.map.service;

import com.zynboot.map.infrastructure.entity.MapSourceProxy;
import com.zynboot.map.infrastructure.mapper.MapSourceProxyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代理健康检查服务（定时执行）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyHealthCheckService {

    private final MapSourceProxyMapper proxyMapper;
    private final OkHttpClient httpClient;

    @Scheduled(fixedDelay = 300000) // 每 5 分钟
    public void checkAll() {
        List<MapSourceProxy> proxies = proxyMapper.selectList(null);
        for (MapSourceProxy proxy : proxies) {
            checkOne(proxy);
        }
    }

    public void checkOne(MapSourceProxy proxy) {
        String url = proxy.getUrl();
        if (url == null || url.isBlank()) return;

        try {
            // 尝试获取最小瓦片
            String testUrl = url.replaceAll("/$", "") + "/0/0/0.png";
            Request request = new Request.Builder().url(testUrl).head().build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    proxy.setHealthStatus("HEALTHY");
                    proxy.setFailCount(0);
                } else {
                    proxy.setHealthStatus("DEGRADED");
                    proxy.setFailCount(proxy.getFailCount() + 1);
                    proxy.setHealthMessage("HTTP " + response.code());
                }
            }
        } catch (Exception e) {
            proxy.setFailCount(proxy.getFailCount() + 1);
            proxy.setHealthMessage(e.getMessage());
            if (proxy.getFailCount() >= 5) {
                proxy.setHealthStatus("DOWN");
            }
        }

        proxy.setLastCheckAt(LocalDateTime.now());
        proxyMapper.updateById(proxy);
    }
}
