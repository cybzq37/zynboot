package com.zynboot.infra.web.version;

import java.lang.annotation.*;

/**
 * API 版本注解。
 * <p>
 * 标注在 Controller 类或方法上，自动映射到 URL 路径前缀 {@code /v{version}}。
 *
 * <pre>
 * {@literal @}RestController
 * {@literal @}RequestMapping("/user")
 * {@literal @}ApiVersion("1")
 * public class UserV1Controller { ... }   // → /v1/user
 *
 * {@literal @}RestController
 * {@literal @}RequestMapping("/user")
 * {@literal @}ApiVersion("2")
 * public class UserV2Controller { ... }   // → /v2/user
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {

    /**
     * 版本号（如 "1", "2", "3"）。
     */
    String value();
}
