package com.zynboot.infra.web.version;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API 版本控制自动配置。
 * <p>
 * 通过 {@link WebMvcRegistrations} 注册 {@link ApiVersionRequestMappingHandlerMapping}，
 * 使 {@link ApiVersion} 注解生效，同时不影响 Spring Boot 的 {@code WebMvcAutoConfiguration}。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "zyn.web.version", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiVersionAutoConfiguration {

    @org.springframework.context.annotation.Bean
    WebMvcRegistrations apiVersionRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new ApiVersionRequestMappingHandlerMapping();
            }
        };
    }
}
