package com.zynboot.infra.seata;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Seata 分布式事务自动配置。
 * <p>
 * 默认关闭（zyn.seata.enabled=false），不影响服务启动。
 * 开启后接入 Seata TC，支持 @GlobalTransactional 注解。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "zyn.seata", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class SeataAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Seata distributed transaction enabled (AT mode)");
    }
}
