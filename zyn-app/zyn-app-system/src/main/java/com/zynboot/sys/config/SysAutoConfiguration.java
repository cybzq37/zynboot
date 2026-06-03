package com.zynboot.sys.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.zynboot.infra.mybatis.config.MybatisAutoConfiguration;
import com.zynboot.infra.satoken.config.SaTokenConfig;
import com.zynboot.sys.handler.query.PermissionQueryHandler;
import com.zynboot.sys.config.SaPermissionDelegate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = {SaTokenConfig.class, MybatisAutoConfiguration.class})
@MapperScan("com.zynboot.sys.infrastructure.mapper")
public class SysAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(StpInterface.class)
    public StpInterface stpInterface(PermissionQueryHandler permissionManager) {
        return new SaPermissionDelegate(permissionManager);
    }

    @Bean
    public BeanPostProcessor optimisticLockerProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof MybatisPlusInterceptor interceptor) {
                    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
                }
                return bean;
            }
        };
    }
}
