package com.zyn.infra.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * 扫描所有 {@link ExchangeClient @ExchangeClient} 标记的接口，
 * 自动注册为 HttpExchange 代理 Bean。
 */
@Slf4j
public class ExchangeClientRegistrar implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    @Override
                    protected boolean isCandidateComponent(
                            org.springframework.beans.factory.annotation.AnnotatedBeanDefinition beanDefinition) {
                        return true;
                    }
                };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ExchangeClient.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.zyn");

        for (BeanDefinition bd : candidates) {
            String className = bd.getBeanClassName();
            if (className == null) continue;

            try {
                Class<?> clientType = Class.forName(className);
                ExchangeClient anno = clientType.getAnnotation(ExchangeClient.class);
                if (anno == null) continue;

                String serviceName = anno.value();
                String beanName = className + "Proxy";

                if (registry.containsBeanDefinition(beanName)) continue;

                AbstractBeanDefinition proxyBd = BeanDefinitionBuilder
                        .genericBeanDefinition(ExchangeClientFactoryBean.class)
                        .addConstructorArgValue(clientType)
                        .addConstructorArgValue(serviceName)
                        .setScope(BeanDefinition.SCOPE_SINGLETON)
                        .getBeanDefinition();
                proxyBd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                registry.registerBeanDefinition(beanName, proxyBd);

                log.info("Discovered service client: {} -> {}", clientType.getSimpleName(), serviceName);
            } catch (ClassNotFoundException e) {
                log.warn("Failed to load service client class: {}", className, e);
            }
        }
    }
}
