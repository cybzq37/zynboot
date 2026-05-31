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

在wsl环境中进行测试：
1. 部署应用，在容器中部署 zyn-app-system，zyn-app-demo 引入依赖 zyn-api-system，并在容器中部署
2. 部署成功后，zyn-app-demo 调用 zyn-app-system的api接口进行测试
3. 部署时需要注意，maven需要指定为prod环境，所有中间件都已经再wsl中部署好，只需要部署应用到容器进行测试
4. 所有system-api的接口都要在demo中mock测试一遍，有问题就修改，mock测试可以使用python bash脚本，java unit test，或者你觉得合适任何语言或第三方工具

先给出方案


  步骤拆解

  Step 1：Maven 构建（Windows 侧）
  mvn clean package -pl zyn-app/zyn-app-system,zyn-app/zyn-app-demo -am -P prod -DskipTests

  Step 2：部署两个应用容器
  # 用现有 build.sh 脚本，复制 JAR 到 WSL 后执行
  ./deploy/app/build.sh --jar zyn-app-system.jar --name sys --port 28081 --profile prod
  ./deploy/app/build.sh --jar zyn-app-demo.jar --name demo --port 28080 --profile prod

  Step 4：等待启动完成 + 健康检查

  Step 5：测试（Python pytest 脚本）

  测试分两层：

  ┌──────────┬─────────────────────────────────────────────┬─────────────────────────────┐
  │   层次   │                  测试目标                   │            方式             │
  ├──────────┼─────────────────────────────────────────────┼─────────────────────────────┤
  │ 直连测试 │ 直接调 zyn-app-system:28081 的所有 API      │ curl/pytest → system        │
  ├──────────┼─────────────────────────────────────────────┼─────────────────────────────┤
  │ 代理测试 │ 通过 zyn-app-demo:28080 调用 Remote*Service │ curl/pytest → demo → system │
  └──────────┴─────────────────────────────────────────────┴─────────────────────────────┘

  测试脚本设计（Python pytest）

  tests/
    conftest.py            # base_url, login 获取 token, 通用 fixture
    test_auth.py           # POST /login, POST /logout, GET /info
    test_user.py           # CRUD + page
    test_role.py           # CRUD + list
    test_permission.py     # CRUD + list + tree
    test_resource.py       # CRUD + list
    test_organization.py   # CRUD + list + tree
    test_remote_user.py    # 通过 demo 代理调 RemoteUserService

  测试策略：每个域按顺序跑 CRUD，例如 user：
  1. POST /api/v1/user — 创建，记录返回的 id
  2. GET /api/v1/user — 分页查询，验证刚创建的记录存在
  3. GET /api/v1/user/{id} — 单条查询，验证字段
  4. PUT /api/v1/user/{id} — 更新，再查询验证
  5. DELETE /api/v1/user/{id} — 删除，再查询验证已删除

  Auth 流程：
  - 用 seed data 的 root 用户登录获取 token（zyn.satoken.root-user-id）
  - 后续请求带 satoken header

  需要确认的问题

  1. WSL 中间件是否已经启动？ 还是需要从头启动？
  2. prod 配置中的 IP 192.168.1.3 是否是 WSL 的实际 IP？还是需要改成其他地址（如 host.docker.internal 或容器名）？
  3. proxy 测试层：当前 SysDemoController 只代理了 RemoteUserService 的 2 个方法。要测试全部 Remote*Service，需要给 demo 加对应的 proxy
  端点。你希望我一起加上，还是先只测直连层？

现在可以进行测试了：
  1. wsl中中间件已启动
  2. prod中配置的IP为真实IP，不需要修改
  3. proxy 测试层：demo 要测试 zyn-api-system 中的全部 api，进行补全
  4. 测试流程说明和脚本固化下来，方便以后执行