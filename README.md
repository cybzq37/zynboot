# zyn

Java 21 + Spring Boot 3.5.x 多模块框架

## 模块结构

```
zyn/
├── zyn-kit/          ← 工具层（工具类、Jackson、OkHttp、线程池、树结构）
├── zyn-conf/         ← 配置层（环境配置、日志、中间件连接）
├── zyn-infra/        ← 基础设施层
│   ├── zyn-infra-discovery   ← 服务发现（HttpExchange 自动注册）
│   ├── zyn-infra-redis       ← Redis 客户端
│   ├── zyn-infra-kafka       ← Kafka 客户端
│   ├── zyn-infra-es          ← Elasticsearch 客户端
│   ├── zyn-infra-mybatis     ← MyBatis-Plus 扩展
│   ├── zyn-infra-storage     ← 文件存储（本地/S3）
│   ├── zyn-infra-geo         ← GIS（Shapefile/GeoJSON/CRS/GDAL）
│   ├── zyn-infra-web         ← Web 基础（全局异常、响应包装、CORS）
│   └── zyn-infra-satoken     ← Sa-Token 认证
├── zyn-api/          ← API 契约层（DTO/Query/HttpExchange 接口）
│   └── zyn-api-system        ← 系统管理 API
└── zyn-app/          ← 应用层（业务实现）
    ├── zyn-app-system        ← 系统管理服务
    ├── zyn-app-netty         ← Netty 服务
    └── zyn-app-demo          ← 示例应用
```


## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.5.x |
| 数据库 | PostgreSQL + PostGIS |
| 缓存 | Redis |
| 消息队列 | Kafka |
| 搜索引擎 | Elasticsearch + IK/Pinyin/STConvert |
| 对象存储 | SeaweedFS (S3 兼容) |
| ORM | MyBatis-Plus |
| 认证 | Sa-Token |
| 文档 | SpringDoc (Swagger) |
| 日志 | Log4j2 + Disruptor |
| HTTP 客户端 | OkHttp + HttpExchange |
| GIS | GeoTools + GDAL |

## 构建

```bash
# 开发环境
mvn clean package

# 生产环境
mvn clean package -P prod
```

## 中间件

```bash
cd deploy/middleware
bash scripts/start.sh           # 启动全部
bash scripts/start.sh postgres  # 启动指定服务
bash scripts/start.sh -f redis  # 重建指定服务
bash scripts/healthcheck.sh     # 健康检查
```

## 部署

```bash
cd deploy/app
./build.sh --jar zyn-app-demo.jar --name demo --port 28080
```



## API 设计

接口风格 restful，架构模式 CQRS，DDD建模
采用 CQRS（Command Query Responsibility Segregation）风格定义：

  Query（读）→ 返回数据，不修改状态
  Command（写）→ 修改状态，不返回数据（REST 中可返回结果）

  你的外部设计符合 CQRS：

  Query   → GET 读操作（UserQuery）
  Cmd     → POST/PUT 写操作（UserSaveCmd、ResetPasswordCmd）
  Res     → 响应数据（UserRes）

```
com.zyn.api.sys/
├── client/      ← HttpExchange 远程调用接口
├── dto/         ← 响应对象（DTO/VO 统一）
└── query/       ← 请求参数
```


外部交互 DTO（CQRS 风格）
如果是查询，统一后缀 UserQuery
如果是增加或修改 UserSaveCmd，其他的动作也可以定义 ResetPasswordCmd、AssignRoleCmd
如果是实体，统一后缀 Entity
如果是删除，直接传id或列表

整体目录结构：
entity/
├── SysUserEntity
├── SysRoleEntity
├── SysOrgEntity
query/
├── UserQuery
├── RoleQuery
├── OrgQuery
command/
├── UserSaveCmd
├── ResetPasswordCmd
├── AssignRoleCmd
├── RoleSaveCmd
├── OrgSaveCmd
response/
├── UserRes
├── UserDetailRes
├── RoleRes
├── OrgRes

query command response 都是外部交互的DTO
内部的DTO如何定义

结算结果 xxxResult
上下文 xxxContext
事件 xxxEvent
配置 xxxConfig xxxProperties
统计 xxxStatistics
状态 xxxState
内部条件封装 UserSpec OrderQueryCriteria
记录 XXXRecord
消息 payload

