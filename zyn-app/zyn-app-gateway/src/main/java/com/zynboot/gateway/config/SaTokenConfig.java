package com.zynboot.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Configuration
public class SaTokenConfig {

    @Value("${zyn.gateway.auth.exclude-paths:/sys/api/v1/auth/login,/actuator/**,/v3/api-docs/**,/swagger-ui/**,/swagger-ui.html}")
    private List<String> excludePaths;

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", "401", "message", "Unauthorized: " + e.getMessage()))));

        excludePaths.forEach(filter::addExclude);
        return filter;
    }
}
