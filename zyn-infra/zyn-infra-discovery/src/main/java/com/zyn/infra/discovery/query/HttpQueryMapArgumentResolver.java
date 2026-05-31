package com.zyn.infra.discovery.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

import java.util.Collection;
import java.util.Map;

/**
 * 将标注了 {@link HttpQueryMap} 的 POJO 参数展开为 URL 查询参数。
 * <p>
 * 通过 ObjectMapper 将 POJO 转为 Map，再逐个添加为 requestParameter。
 */
public class HttpQueryMapArgumentResolver implements HttpServiceArgumentResolver {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean resolve(Object argument, MethodParameter parameter,
                           HttpRequestValues.Builder requestValues) {
        if (argument == null || !parameter.hasParameterAnnotation(HttpQueryMap.class)) {
            return false;
        }

        Map<String, Object> map = MAPPER.convertValue(argument, new TypeReference<>() {});

        map.forEach((key, value) -> {
            if (value == null) return;
            if (value instanceof Collection<?> col) {
                for (Object item : col) {
                    requestValues.addRequestParameter(key, String.valueOf(item));
                }
            } else {
                requestValues.addRequestParameter(key, String.valueOf(value));
            }
        });
        return true;
    }
}
