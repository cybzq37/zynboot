package com.zynboot.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.zynboot")
@EnableFeignClients(basePackages = "com.zynboot.sys.api")
public class ZynSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZynSysApplication.class, args);
    }
}
