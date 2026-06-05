package com.zynboot.infra.web.version;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * 支持 {@link ApiVersion} 注解的 RequestMappingHandlerMapping。
 * <p>
 * 将 {@code @ApiVersion("1")} 映射为 URL 前缀 {@code /v1}，
 * 使得 {@code @RequestMapping("/api/user") + @ApiVersion("1")}
 * 实际匹配 {@code /api/v1/user}。
 */
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) return null;

        ApiVersion methodVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
        ApiVersion classVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
        ApiVersion version = methodVersion != null ? methodVersion : classVersion;

        if (version != null) {
            String prefix = "/v" + version.value();
            RequestMappingInfo prefixInfo = RequestMappingInfo.paths(prefix).build();
            return prefixInfo.combine(info);
        }
        return info;
    }
}
