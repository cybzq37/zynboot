# zyn-api-system

系统管理模块 API 契约层，定义接口、响应、查询、命令对象，供服务端和客户端共用。

## 包结构（CQRS 风格）

```
com.zyn.api.sys/
├── client/              ← HttpExchange 远程调用接口
│   └── RemoteUserService.java
├── response/            ← 响应对象（Res 后缀）
│   ├── user/
│   │   ├── UserRes.java
│   │   ├── LoginUserRes.java
│   │   ├── LoginRes.java
│   │   └── UserInfoRes.java
│   ├── role/
│   │   └── RoleRes.java
│   ├── permission/
│   │   ├── PermissionRes.java
│   │   └── MenuTreeRes.java
│   └── org/
│       ├── OrgRes.java
│       └── OrgTreeRes.java
├── query/               ← 查询参数（Query 后缀）
│   ├── user/
│   │   ├── UserQuery.java
│   │   └── UserPageQuery.java
│   ├── role/
│   │   └── RoleQuery.java
│   └── permission/
│       └── PermissionQuery.java
└── command/             ← 写操作（Cmd 后缀）
    └── user/
        └── UserSaveCmd.java
```

## 命名规范

| 类型 | 后缀 | 用途 | 示例 |
|------|------|------|------|
| 响应对象 | `Res` | API 响应 | `UserRes`、`LoginRes` |
| 查询参数 | `Query` | GET 请求参数 | `UserQuery`、`UserPageQuery` |
| 写命令 | `Cmd` | POST/PUT 请求体 | `UserSaveCmd`、`ResetPasswordCmd` |
| HttpExchange | `Remote*Service` | 跨服务调用 | `RemoteUserService` |

## 使用示例

```java
// 查询
@GetMapping("/list")
public List<UserRes> list(UserQuery query) { ... }

// 创建/更新
@PostMapping
public void save(@RequestBody UserSaveCmd cmd) { ... }

// 远程调用
@ServiceClient("sys")
@HttpExchange("/api/v1/user")
public interface RemoteUserService {
    @GetExchange("/{id}")
    ApiResponse<UserRes> getById(@PathVariable String id);
}
```
