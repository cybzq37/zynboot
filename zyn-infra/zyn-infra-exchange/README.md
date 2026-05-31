# zyn-infra-discovery

服务发现与 HttpExchange 客户端自动配置模块。引入依赖后自动扫描 `@ServiceClient` 接口，无需手动编写配置类。

## 快速开始

### Step 1：添加依赖

```xml
<dependency>
    <groupId>com.zyn</groupId>
    <artifactId>zyn-infra-discovery</artifactId>
</dependency>
```

### Step 2：注册服务地址

在 `zyn-services.yml` 中添加服务地址：

```yaml
zyn:
  discovery:
    services:
      sys: http://zyn-sys:8081
      biz: http://zyn-biz:8082
```

### Step 3：定义接口

```java
@ServiceClient("sys")
@HttpExchange("/api/v1/user")
public interface RemoteUserService {

    @GetExchange("/{id}")
    ApiResponse<UserDTO> getById(@PathVariable String id);
}
```

### Step 4：直接注入使用

```java
@RestController
@RequiredArgsConstructor
public class DemoController {

    private final RemoteUserService userService;

    @GetMapping("/demo/{id}")
    public ApiResponse<UserDTO> demo(@PathVariable String id) {
        return userService.getById(id);
    }
}
```

不需要写任何 `@Configuration` 类或 `@Bean` 方法。

---

## 环境切换

默认地址定义在 `zyn-services.yml`（Docker 环境）。通过 Spring Profile 覆盖：

```yaml
# application-dev.yml（本地调试）
zyn:
  discovery:
    services:
      sys: http://127.0.0.1:8081
```

```yaml
# application-prod.yml（生产环境）
zyn:
  discovery:
    services:
      sys: https://sys.zyn.com
```

只覆盖需要修改的服务，未覆盖的继续使用 `zyn-services.yml` 的默认值。

---

## 代码逻辑

启动时按以下顺序自动执行：

```
① ServiceDiscoveryPostProcessor
   ↓ 加载 classpath:zyn-services.yml 到 Spring Environment

② DiscoveryAutoConfiguration
   ↓ 读取 zyn.discovery.services → DiscoveryProperties
   ↓ 将 props 存入 DiscoveryPropertiesHolder（静态持有）

③ ServiceClientRegistrar
   ↓ 扫描 com.** 下所有 @ServiceClient 接口
   ↓ 注册 ServiceClientFactoryBean 到容器

④ 首次注入时
   ↓ FactoryBean.getObject()
   ↓ 从 DiscoveryPropertiesHolder 获取服务地址
   ↓ ServiceProxyBuilder 创建 HttpExchange 代理
```

---

## 模块结构

```
zyn-infra-discovery/
└── src/main/
    ├── java/com/zyn/infra/discovery/
    │   ├── ServiceClient.java               # 注解：标记接口对应的服务名
    │   ├── DiscoveryProperties.java         # 配置属性：读取 zyn.discovery.services
    │   ├── DiscoveryPropertiesHolder.java   # 静态持有者：供 FactoryBean 获取 props
    │   ├── ServiceProxyBuilder.java         # 代理构建器：RestClient → HttpExchange 代理
    │   ├── ServiceClientRegistrar.java      # 扫描注册器：扫描 @ServiceClient 并注册 Bean
    │   ├── ServiceDiscoveryPostProcessor.java  # 环境后处理器：自动加载 zyn-services.yml
    │   └── DiscoveryAutoConfiguration.java  # 自动配置入口
    └── resources/
        ├── zyn-services.yml                 # 默认服务地址配置
        ├── META-INF/
        │   ├── spring.factories             # 注册 EnvironmentPostProcessor
        │   └── spring/
        │       └── AutoConfiguration.imports  # 注册 AutoConfiguration
```

---

## 组件说明

| 组件 | 职责 |
|------|------|
| `@ServiceClient` | 标注在 `@HttpExchange` 接口上，声明对应的服务名 |
| `DiscoveryProperties` | 读取 `zyn.discovery.services` 配置，提供 `getRequiredUrl()` |
| `ServiceClientRegistrar` | 扫描所有 `@ServiceClient` 接口，注册为 `ServiceClientFactoryBean` |
| `ServiceClientFactoryBean` | 延迟创建 HttpExchange 代理，首次注入时才实例化 |
| `ServiceProxyBuilder` | `RestClient` + `HttpServiceProxyFactory` → 代理对象 |
| `ServiceDiscoveryPostProcessor` | 自动加载 `zyn-services.yml`，无需 `spring.config.import` |
| `DiscoveryAutoConfiguration` | 激活配置、持有 `DiscoveryProperties`、触发扫描 |

---

## 禁用模块

```yaml
zyn:
  discovery:
    enabled: false
```
