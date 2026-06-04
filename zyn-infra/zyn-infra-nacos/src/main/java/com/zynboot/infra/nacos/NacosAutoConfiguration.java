package com.zynboot.infra.nacos;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@Slf4j
public class NacosAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Nacos service discovery and config center enabled");
    }
}
