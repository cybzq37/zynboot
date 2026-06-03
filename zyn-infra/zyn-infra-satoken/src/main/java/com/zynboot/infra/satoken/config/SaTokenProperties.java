package com.zynboot.infra.satoken.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sa-Token 配置属性
 *
 * @author lichunqing
 */
@Data
@ConfigurationProperties(prefix = "zyn.satoken")
public class SaTokenProperties {

    /**
     * 登录类型标识
     */
    private String loginType = "login";

    /**
     * Token 前缀
     */
    private String tokenPrefix = "Bearer";

    /**
     * 超级管理员用户ID
     */
    private Long rootUserId = 1L;
}
