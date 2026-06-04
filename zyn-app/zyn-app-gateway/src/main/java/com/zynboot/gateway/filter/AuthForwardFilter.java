package com.zynboot.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthForwardFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String resolvedUserId = null;
        try {
            if (StpUtil.isLogin()) {
                resolvedUserId = String.valueOf(StpUtil.getLoginId());
            }
        } catch (Exception ignored) {
        }

        final String userId = resolvedUserId;
        ServerWebExchange.Builder builder = exchange.mutate();
        if (userId != null) {
            builder.request(r -> r.header("X-User-Id", userId));
        }
        return chain.filter(builder.build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
