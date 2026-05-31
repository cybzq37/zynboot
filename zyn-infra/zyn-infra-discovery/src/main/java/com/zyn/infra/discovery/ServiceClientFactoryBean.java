package com.zyn.infra.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * FactoryBean：根据服务名从配置获取地址，创建 HttpExchange 代理。
 * 通过 Environment 读取 zyn.discovery.services.<serviceName>，避免依赖 Bean 生命周期顺序。
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

        this.proxy = ServiceProxyBuilder.build(
                clientType, url, connectTimeoutMs, readTimeoutMs, followRedirects);
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
