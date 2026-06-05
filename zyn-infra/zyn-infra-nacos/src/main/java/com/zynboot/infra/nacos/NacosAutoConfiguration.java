package com.zynboot.infra.nacos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Nacos 服务发现与配置中心自动配置。
 * <p>
 * 引入 {@code spring-cloud-starter-alibaba-nacos-discovery} 和
 * {@code spring-cloud-starter-alibaba-nacos-config} 依赖，
 * 通过 {@code zyn.nacos.enabled=true} 启用（默认关闭）。
 * <p>
 * 实际自动配置由 Spring Cloud Alibaba Starter 完成，本模块仅控制依赖引入。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "zyn.nacos", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class NacosAutoConfiguration {
}
