package com.zynboot.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
public class SaTokenConfig {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(
                        "/sys/api/v1/auth/login",
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                )
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", "401", "message", "Unauthorized: " + e.getMessage()))));
    }
}
