# 微服务框架改进清单

## 高优先级（生产必备）

| 改进项 | 说明 | 涉及模块 |
|--------|------|----------|
| **Sentinel 熔断降级** | Feign 调用熔断、限流、降级 fallback | `zyn-infra-feign` + `zyn-app-gateway` |
| **链路追踪** | SkyWalking（Java Agent 零代码接入），存储复用 ES | 全局 + `deploy/middleware` |
| **Feign 调用失败 fallback** | `@FeignClient(fallbackFactory=...)` 返回降级响应 | `zyn-api-system` |
| **包名一致性** | `zyn-infra-feign` 内部包名仍是 `com.zynboot.infra.exchange`，应改为 `com.zynboot.infra.feign` | `zyn-infra-feign` |
| **类名一致性** | `ExchangeAutoConfiguration` → `FeignAutoConfiguration` | `zyn-infra-feign` |

## 中优先级（运维便利）

| 改进项 | 说明 |
|--------|------|
| **Gateway 限流** | Sentinel 或 Redis + Lua 令牌桶，防刷防 DDoS |
| **Swagger 聚合** | Gateway 聚合各服务 API 文档，统一入口 |
| **配置热刷新** | `@RefreshScope` + Nacos Config 监听，配置变更无需重启 |
| **健康检查聚合** | Gateway `/actuator/health` 聚合下游服务健康状态 |
| **统一异常格式** | Gateway 层 `WebFilter` 捕获异常返回标准 `ErrorResponse` |

## 低优先级（架构演进）

| 改进项 | 说明 |
|--------|------|
| **分布式事务** | Seata（AT/TCC 模式），跨服务写操作一致性 |
| **消息驱动** | Kafka + Spring Cloud Stream，服务间异步通信 |
| **灰度发布** | Sentinel + Nacos 元数据，按比例/标签路由 |
| **API 版本控制** | `/api/v1/` vs `/api/v2/` 路由策略 |
