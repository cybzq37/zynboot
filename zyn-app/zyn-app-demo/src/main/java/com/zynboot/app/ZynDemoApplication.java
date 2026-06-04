package com.zynboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.zynboot")
@EnableFeignClients(basePackages = "com.zynboot.sys.api")
public class ZynDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZynDemoApplication.class, args);
    }
}
