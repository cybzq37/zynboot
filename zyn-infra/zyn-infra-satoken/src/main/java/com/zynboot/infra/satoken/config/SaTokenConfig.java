package com.zynboot.infra.satoken.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import com.zynboot.infra.redis.RedisClient;
import com.zynboot.infra.satoken.dao.RedisSaTokenDao;
import com.zynboot.infra.satoken.service.SaPermissionImpl;
import com.zynboot.infra.satoken.utils.LoginHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Sa-Token 自动配置
 * <p>
 * 通过 {@code zyn.infra.satoken.enabled=true} 启用（默认开启）
 *
 * @author lichunqing
 */
@AutoConfiguration
@ConditionalOnClass(StpLogic.class)
@ConditionalOnProperty(prefix = "zyn.satoken", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SaTokenProperties.class)
public class SaTokenConfig {

    @Bean
    @ConditionalOnMissingBean(StpLogic.class)
    public StpLogic stpLogic(SaTokenProperties properties) {
        return new StpLogic(properties.getLoginType());
    }

    @Bean
    @ConditionalOnMissingBean(StpInterface.class)
    public StpInterface stpInterface() {
        return new SaPermissionImpl();
    }

    @Bean
    @ConditionalOnBean(RedisClient.class)
    @ConditionalOnMissingBean(SaTokenDao.class)
    public SaTokenDao saTokenDao(RedisClient redisTemplate) {
        return new RedisSaTokenDao(redisTemplate);
    }

    @Bean
    LoginHelper loginHelperInitializer(SaTokenProperties properties) {
        LoginHelper.setProperties(properties);
        return new LoginHelper();
    }
}
