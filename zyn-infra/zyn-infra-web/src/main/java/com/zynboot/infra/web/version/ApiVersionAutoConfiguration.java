package com.zynboot.infra.web.version;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API 版本控制自动配置。
 * <p>
 * 注册 {@link ApiVersionRequestMappingHandlerMapping} 替代默认的 HandlerMapping，
 * 使 {@link ApiVersion} 注解生效。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ApiVersionAutoConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}
