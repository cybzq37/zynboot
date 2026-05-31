package com.zyn.infra.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * FactoryBean：根据 @HttpExchange.url 创建 HTTP 客户端代理。
 * 支持 Spring 属性占位符（如 ${api.sys.base-url}）。
 * <p>
 * 自动通过 SPI 加载 {@link HttpServiceArgumentResolver} 实现。
 */
@Slf4j
public class ServiceClientFactoryBean implements FactoryBean<Object>, EnvironmentAware, InitializingBean {

    private final Class<?> clientType;
    private final String serviceName;

    private Environment environment;
    private Object proxy;

    public ServiceClientFactoryBean(Class<?> clientType, String serviceName) {
        this.clientType = clientType;
        this.serviceName = serviceName;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        String url = environment.getProperty("zyn.discovery.services." + serviceName);
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "Service URL not configured: zyn.discovery.services." + serviceName);
        }

        long connectTimeoutMs = environment.getProperty(
                "zyn.discovery.connect-timeout-ms", Long.class, 3000L);
        long readTimeoutMs = environment.getProperty(
                "zyn.discovery.read-timeout-ms", Long.class, 10000L);
        boolean followRedirects = environment.getProperty(
                "zyn.discovery.follow-redirects", Boolean.class, false);

        List<HttpServiceArgumentResolver> resolvers = new ArrayList<>();
        for (HttpServiceArgumentResolver resolver : ServiceLoader.load(HttpServiceArgumentResolver.class)) {
            resolvers.add(resolver);
        }

        this.proxy = ServiceProxyBuilder.build(
                clientType, url, connectTimeoutMs, readTimeoutMs, followRedirects, resolvers);
        log.info("Created service client: {} -> {} ({})", clientType.getSimpleName(), serviceName, url);
    }

    @Override
    public Object getObject() {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return clientType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
