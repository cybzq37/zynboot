package com.zynboot.infra.exchange;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * OpenFeign 自动配置。
 * 注册鉴权转发拦截器，将当前请求的 Authorization 头传递给下游服务。
 */
@AutoConfiguration
@ConditionalOnClass(name = "feign.Feign")
@ConditionalOnProperty(prefix = "zyn.feign", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExchangeAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication
    public RequestInterceptor feignAuthRequestInterceptor() {
        return new FeignAuthRequestInterceptor();
    }

    @Slf4j
    static class FeignAuthRequestInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletAttrs) {
                HttpServletRequest request = servletAttrs.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isBlank()) {
                    template.header("Authorization", authHeader);
                }
                String userId = request.getHeader("X-User-Id");
                if (userId != null && !userId.isBlank()) {
                    template.header("X-User-Id", userId);
                }
            }
        }
    }
}
