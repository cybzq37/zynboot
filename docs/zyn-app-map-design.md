# zyn-app-map 模块设计文档

## 一、功能清单

### 1.1 核心功能

| # | 功能 | 说明 | 优先级 |
|---|------|------|--------|
| 1 | 图层管理 | 图层 CRUD，支持矢量/栅格两种类型 | P0 |
| 2 | 图层分组管理 | 树形目录，支持多级嵌套 | P0 |
| 3 | 图层属性设置 | 字段定义（名称、类型、别名、可见性、排序） | P0 |
| 4 | 图层样式设置 | JSON 存储，支持点/线/面/栅格渲染规则 | P0 |
| 5 | 图层数据管理 | 要素增删改查，属性编辑 | P0 |
| 6 | 矢量数据导入 | CSV / GeoJSON / Shapefile 导入，坐标自动转换 | P0 |
| 7 | 栅格数据支持 | 栅格文件存磁盘（双通道：API 上传 / 手动部署），图层记录元数据 | P0 |
| 8 | 数据源配置 | 一图层多数据源，支持 FILE / PostGIS / WMS / WFS / WMTS / TMS / XYZ | P0 |
| 9 | 底图管理 | 底图 CRUD，支持 XYZ / WMS / WMTS / TMS，含代理鉴权 | P0 |
| 10 | 空间查询 | 框选、圆选、多边形选、点选（ST_Intersects / ST_Within） | P0 |
| 11 | 属性查询 | 按 JSONB 字段条件筛选要素 | P0 |
| 12 | 坐标系管理 | 图层绑定 CRS，导入时自动检测/转换，常用坐标系预设 | P0 |
| 13 | 矢量导出 | 按图层/筛选条件导出 GeoJSON / Shapefile / CSV | P0 |
| 14 | 空间索引 | PostGIS GiST 索引，加速空间查询 | P0 |
| 15 | 流式分页 | 大数据量要素查询采用游标/流式返回，避免 OOM | P0 |
| 16 | 图层元数据 | 数据来源、采集时间、精度、覆盖范围、要素数量 | P0 |

### 1.2 增强功能

| # | 功能 | 说明 | 优先级 |
|---|------|------|--------|
| 17 | 外部服务代理 | 代理 WMS/WMTS/TMS/XYZ 外部瓦片服务，Redis 缓存 + 健康监控 | P1 |
| 18 | PostGIS 直查 | 外部数据库直查模式，数据不导入 map_feature，`external_*` 字段 + 查询路由 | P1 |
| 19 | 在线投影转换 | 查询时按目标 CRS 动态投影（`?projection=EPSG:4490`） | P1 |
| 20 | 空间分析 | 缓冲区分析、叠加分析（交集/并集/裁剪）、距离量算、面积量算 | P1 |
| 21 | 栅格切片 | 大栅格异步切片为 XYZ 瓦片 + 金字塔概览 | P1 |
| 22 | 栅格样式 | 色带映射、透明度、nodata 设置、拉伸方式 | P1 |
| 23 | 矢量瓦片 | 生成 MVT（Mapbox Vector Tile）/ Protobuf 瓦片 | P1 |
| 24 | 数据缓存 | 瓦片/查询结果 Redis 缓存，可配置 TTL | P1 |
| 25 | 地图实例配置 | `map_instance` 表，中心点/缩放/底图/图层叠加顺序 | P1 |
| 26 | 图层权限 | 基于 RBAC 控制图层读/写/发布权限 | P1 |
| 27 | 数据质量校验 | 导入时校验几何有效性（ST_IsValid / ST_MakeValid）、坐标范围 | P1 |
| 28 | 要素聚合 | 聚类查询（ST_ClusterKMeans），返回聚类中心 + 要素数量 | P2 |
| 29 | 地图发布 | `map_publish` 表（PUBLIC），公开访问入口 | P2 |
| 30 | 图层版本 | `map_layer_version` 表，导入前自动快照 + 按版本回滚整个图层 | P2 |
| 31 | 操作审计 | `map_operation_log` 表，记录所有 CRUD/导入/导出操作 | P2 |
| 32 | 变更日志 | 谁在什么时间修改了哪些要素 | P2 |
| 33 | 图层公开/私有 | `map_layer.is_public` + `map_publish` 公开访问入口 | P2 |
| 34 | 要素级权限 | 按用户/角色控制可编辑的要素范围 | P2 |
| 35 | 缩略图生成 | 地图/图层自动生成缩略图预览 | P2 |

---

## 二、数据模型

> **核心设计**：一图层多数据源（`map_layer_source`），每次导入为独立 source，各自有源 CRS。
> 导入时统一转换到图层目标 CRS（`target_srid`），要素归属到具体 source。
>
> **删除策略**：全部物理删除。删除图层时级联删除所有关联数据库数据，磁盘文件保留不删。

### 2.1 基础表

```
map_layer_group           图层分组（树形，邻接表）
├── id, parent_id (NULL=根), name, description, sort_order (SMALLINT)
├── icon, color
└── create_by, create_time, update_by, update_time

map_basemap               底图配置（~20 行）
├── id, name, description
├── type                  XYZ / WMS / WMTS / TMS / VECTOR_TILE
├── url                   瓦片地址模板
├── srid                  SMALLINT（EPSG 数字，如 3857）
├── attribution
├── min_zoom, max_zoom    SMALLINT
├── thumbnail_url, is_default, sort_order
├── wms_layers, wmts_layer, wmts_style, wmts_matrix_set  （非该类型时 NULL）
└── create_by, create_time, update_by, update_time

map_layer                 图层（~500 行）
├── id, group_id, name, title, description
├── type                  RASTER / VECTOR
├── target_srid           SMALLINT（EPSG 数字）
├── geometry_type         点/线/面/多点
├── extent                GEOMETRY(Polygon)（图层范围，取代 VARCHAR）
├── feature_count         INTEGER, source_count SMALLINT
├── render_order          SMALLINT
├── visible, selectable, editable, is_public
├── min_zoom, max_zoom    SMALLINT
├── opacity               NUMERIC(3,2)（0.00~1.00）
├── style_id, metadata (JSONB)
└── create_by, create_time, update_by, update_time

map_layer_field           图层字段定义（~2000 行）
├── id, layer_id, name, alias, type
├── visible, sortable, searchable, sort_order (SMALLINT)
└── UNIQUE(layer_id, name)

map_layer_style           图层样式（~1000 行）
├── id, layer_id, name, type, style_json (JSONB), is_default
└── INDEX(layer_id), INDEX(layer_id, is_default) WHERE is_default
```

### 2.2 数据源表（一图层多数据源）

```
map_layer_source          数据源（~2000 行）
├── id, layer_id, name
├── type                  FILE / POSTGIS / WMS / WFS / WMTS / TMS / XYZ
├── format                CSV / GEOJSON / SHP / GEOTIFF / XYZ（FILE 时）
├── source_srid, target_srid   SMALLINT（EPSG 数字）
├── storage_key           文件存储路径（FILE 时）
├── geometry_type
├── feature_count         INTEGER
├── extent                GEOMETRY(Polygon)（本次导入空间范围）
├── field_mapping         JSONB
├── status                PENDING / PROCESSING / COMPLETED / FAILED
├── message               JSONB（导入校验结果）
├── data_source_id        关联 map_data_source（POSTGIS 时必填）
├── external_schema, external_table, external_geom_col, external_id_col
└── create_by, create_time, update_by, update_time

map_source_raster         栅格专有（~500 行，source_id 为主键）
├── source_id (PK), path
├── width, height, bands (SMALLINT), data_type, nodata
├── pixel_size_x, pixel_size_y
├── compressed_bytes, uncompressed_bytes (BIGINT)

map_source_tile           瓦片专有（~300 行，source_id 为主键）
├── source_id (PK), status, path
├── min_zoom, max_zoom (SMALLINT)
├── format, tile_size (SMALLINT), tile_count, progress (SMALLINT)

map_source_proxy          代理专有（~200 行，source_id 为主键）
├── source_id (PK), url
├── wmts_layer, wmts_style, wmts_matrix_set, wmts_format
├── auth_type, auth_header, auth_value（AES-GCM）
├── cache_ttl (INTEGER)
├── health_status, health_message, last_check_at, fail_count (SMALLINT)

map_data_source           外部数据库连接（~20 行）
├── id, name, type (POSTGIS), url
├── username, password（AES-GCM 加密存储）
├── schema_name, driver_class
├── test_query, status (ACTIVE/INACTIVE)
└── create_by, create_time, update_by, update_time
```

### 2.3 要素与历史

```
map_feature               矢量要素（千万级，BIGINT Snowflake + 哈希 8 分区）
├── id                    BIGINT Snowflake（8 字节，大致有序）
├── layer_id (分区键), source_id
├── properties            JSONB（属性键值对）
├── geometry              GEOMETRY NOT NULL（PostGIS，统一为 target_srid）
└── PK(id, layer_id), INDEX(layer_id), INDEX(source_id), GIST(geometry)

map_layer_version         图层版本（~5000 行）
├── id, layer_id, version
├── name, type (IMPORT/EDIT/MANUAL)
├── source_snapshot       JSONB（导入前 source 快照）
├── feature_count, extent (GEOMETRY), source_count
└── created_by, created_at
    UNIQUE(layer_id, version)
```

### 2.4 运维表

```
map_async_task            异步任务（~10000 行，定期清理）
├── id, type (TILE/RETILE/IMPORT_RASTER)
├── source_id, layer_id
├── status, progress (SMALLINT 0-100)
├── total_count, processed_count, error_count
├── error_message (TEXT), started_at, finished_at
└── created_by, created_at

map_instance              地图实例（~100 行）
├── id, name, description
├── center_lng, center_lat, zoom (SMALLINT)
├── basemap_id, max_extent (GEOMETRY Polygon)
├── is_public
└── create_by, create_time, update_by, update_time

map_instance_layer        实例-图层关联（~2000 行，支持树形分组）
├── id, instance_id
├── parent_id             父节点 ID（NULL = 顶层）
├── is_group              TRUE=分组（layer_id NULL），FALSE=图层叶子
├── layer_id              图层 ID（叶子时必填）
├── name                  显示名称
├── visible, opacity (NUMERIC(3,2)), render_order (SMALLINT)
└── UNIQUE(instance_id, layer_id) WHERE layer_id IS NOT NULL

map_publish               地图发布（~50 行）
├── id, instance_id, type (PUBLIC), is_active
└── create_by, create_time, update_by, update_time
```

### 2.5 实体关系

```
map_layer_group  (1) ──< (N) map_layer
map_instance     (1) ──< (N) map_instance_layer >── (1) map_layer
map_instance     (1) ──< (N) map_publish
map_layer        (1) ──< (N) map_layer_source
map_layer_source (1) ──  (1) map_source_raster / map_source_tile / map_source_proxy
map_layer_source (POSTGIS) ──> (1) map_data_source         外部数据库连接（多 source 共享）
map_layer        (1) ──< (N) map_layer_field
map_layer        (1) ──< (N) map_layer_style
map_layer_source (1) ──< (N) map_feature（FILE 模式）
map_layer_source (POSTGIS) → 外部数据库表（直查模式）
map_layer        (1) ──< (N) map_layer_version
map_async_task   独立表，关联 source_id 或 layer_id
```

### 2.6 级联删除

```
删除图层：
  物理删除 map_layer_source → map_source_raster/tile/proxy → map_layer_field → map_layer_style → map_feature → map_layer_version
  磁盘文件保留不删（管理员手动清理 rm -rf ${root-path}/{layerId}/）

删除数据源：
  物理删除 map_source_raster/tile/proxy → map_feature
  磁盘文件保留不删

版本管理：
  每次批量导入前自动创建 map_layer_version 快照。
  回滚时删除该版本之后的所有 source 及 feature。
  按 created_at 按月分区，定期清理。
```

### 2.7 并发导入安全

```sql
-- 导入时对目标图层加行锁，防止多管理员同时导入竞态
BEGIN;
SELECT * FROM map_layer WHERE id = :layerId FOR UPDATE;
-- INSERT map_feature ...
UPDATE map_layer SET
  feature_count = feature_count + :importedCount,
  extent_bbox = ST_Extent(ST_Union(ST_GeomFromText(:layerBbox), ST_GeomFromText(:importBbox)))
WHERE id = :layerId;
COMMIT;
```

---

## 三、数据导入

### 3.1 矢量导入流程

```
上传文件 (CSV / GeoJSON / SHP)
  → 格式检测 + 大小校验
  → 解析要素集 + 检测源 CRS（SHP .prj / GeoJSON 默认4326）
  → 确定目标图层（新建 or 追加到 layer_id）
  → 创建 map_layer_source（status=PENDING）
  → 字段映射（源字段 → map_layer_field）
  → 逐要素校验 + 坐标转换 + 批量 INSERT map_feature
  → 更新 source/layer 统计
  → import_status → COMPLETED
```

**导入校验（每条要素）：**

| 校验项 | 方法 | 处理 |
|--------|------|------|
| 几何有效性 | `ST_IsValid` | 无效 → `ST_MakeValid`，仍失败 → 跳过 |
| 空几何 | `ST_IsEmpty` / IS NULL | 跳过 |
| 坐标范围 | X ∈ [-180,180], Y ∈ [-90,90] | 越界 → 警告 |
| 几何类型 | vs 图层 geometry_type | 不匹配 → 跳过并记录 |
| SRID 转换 | `ST_Transform` 异常 | 跳过 |

结果写入 `map_layer_source.import_message`（JSON 数组）。

### 3.2 PostGIS 直查注册

```
POST /api/v1/map/import/postgis
{
  "layerId": "xxx",
  "sourceName": "城市建筑（外部库）",
  "dataSourceId": "ds-001",            // 关联 map_data_source（预先配置好连接）
  "externalSchema": "public",          // 可选，覆盖 data_source 的默认 schema
  "externalTable": "buildings",
  "externalGeomCol": "geom",
  "externalIdCol": "gid",
  "sourceSrid": "EPSG:4490"
}

系统：验证连接（通过 map_data_source）→ 查询外部表元数据 → 创建 source（type=POSTGIS）→ 不导入数据
```

### 3.3 栅格导入（双通道）

**通道 A：API 上传（< 500MB）**
```
POST /api/v1/map/import/raster
  file + layerId + sourceSrid + sourceName
  → 写入磁盘目录 → GDAL 读取元数据 → 注册 source
```

**通道 B：手动部署 + 注册（≥ 500MB）**
```
1. scp dem.tif user@server:${root-path}/_inbox/
2. POST /api/v1/map/import/raster/register
     filePath + layerId + sourceSrid + sourceName
3. 校验文件 → 移动到正式目录 → GDAL 读取元数据 → 注册 source
```

**预切片模式（source_format=XYZ）：** 上传 `{z}/{x}/{y}.png` 目录的 zip，解压即用，零切片。

### 3.4 磁盘目录结构

```
${zyn.map.raster.root-path}/
├── _inbox/                           管理员手动放文件的入口
├── {layerId}/{sourceId}/
│   ├── data/                         原始栅格（GEOTIFF 模式）
│   ├── tiles/                        XYZ 瓦片（预切片或系统生成）
│   │   └── {z}/{x}/{y}.png
│   └── metadata.json                 GDAL 元数据缓存
└── _temp/                            API 上传临时目录
```

### 3.5 配置

```yaml
zyn:
  map:
    raster:
      root-path: ./map-data/raster
      upload-max-size: 500MB
      allowed-formats: tif,tiff,img,adf
      auto-tile: true                  # 原始栅格导入后自动异步切片
      auto-overview: true              # 切片时生成金字塔
      tile-format: png
      tile-size: 256
    tile:
      enabled: true
      cache-enabled: true
      cache-ttl: 3600
      allowed-projections: [EPSG:3857, EPSG:4326, EPSG:4490]
```

---

## 四、样式 JSON 结构

```json
{
  "type": "categorized",
  "field": "land_type",
  "categories": [
    {
      "value": "residential",
      "label": "住宅",
      "style": {
        "fillColor": "#FF6B6B",
        "fillOpacity": 0.6,
        "strokeColor": "#333",
        "strokeWidth": 1
      }
    }
  ],
  "default": {
    "fillColor": "#95a5a6",
    "fillOpacity": 0.4,
    "strokeColor": "#333",
    "strokeWidth": 1
  }
}
```

---

## 五、ZXY 瓦片服务

### 5.1 接口

```
GET /api/v1/map/tile/{sourceId}/{z}/{x}/{y}.{format}?projection=EPSG:3857

参数：sourceId, z (0-24), x, y
格式：png / jpeg / webp
投影：默认 3857，可选 4326 / 4490
```

### 5.2 架构（本地 + 代理）

```
请求 /tile/{sourceId}/{z}/{x}/{y}.png
  │
  ├─ Redis 缓存命中 → 直接返回
  │
  ├─ source_type = FILE → 读本地 tiles/{z}/{x}/{y}.png → 投影不匹配则重投影
  │
  ├─ source_type = XYZ/TMS → 构造 external_url/{z}/{x}/{y}.png → 附加鉴权 → HTTP GET
  │   TMS: Y 轴翻转 y = 2^z - 1 - y
  │
  ├─ source_type = WMTS → 构造 GetTile URL（LAYER/STYLE/TILEMATRIXSET/TILEMATRIX/TILEROW/TILECOL）
  │
  ├─ source_type = WMS → z/x/y 计算 BBOX → 构造 GetMap URL
  │
  └─ 写入 Redis 缓存（TTL = proxy_cache_ttl）→ 返回 image
```

**代理鉴权：**

| 类型 | 头部 | 场景 |
|------|------|------|
| NONE | — | 公开服务（OSM） |
| BASIC | `Authorization: Basic {base64}` | 内网 WMTS |
| TOKEN | `Authorization: Bearer {token}` | OAuth2 |
| API_KEY | `{header}: {value}` | 天地图/高德 Key |

**缓存策略：** Key=`cache:tile:{sourceId}:{z}:{x}:{y}:{proj}`，TTL 默认 24h，4xx/5xx 不缓存返回 stale。

### 5.3 健康监控

定时任务（每 5 分钟）检查所有外部服务：
- WMTS/WMS：`GET GetCapabilities`
- XYZ/TMS：`GET /0/0/0.png`
- 连续 5 次失败 → `proxy_health_status = DOWN` → 返回 stale 缓存或 204

### 5.4 多投影

统一按 **EPSG:3857** 切片存储，其他投影在线转换 + Redis 缓存。

---

## 六、模块结构

```
zyn-app-map/
├── src/main/java/com/zynboot/map/
│   ├── controller/
│   │   ├── BasemapController          底图 CRUD
│   │   ├── LayerController            图层 CRUD
│   │   ├── LayerGroupController       分组 CRUD + 树形
│   │   ├── SourceController           图层数据源管理
│   │   ├── DataSourceController       外部数据库连接 CRUD + 测试
│   │   ├── FeatureController          要素 CRUD + 空间查询 + 聚类
│   │   ├── ImportController           数据导入（矢量 + 栅格 + PostGIS 直查）
│   │   ├── ExportController           数据导出
│   │   ├── StyleController            样式管理
│   │   ├── TileController             ZXY 瓦片服务（@IgnoreResponseWrap）
│   │   ├── InstanceController         地图实例 CRUD + 图层树
│   │   ├── PublishController          地图发布 + 公开访问入口
│   │   ├── VersionController          图层版本列表/详情/回滚
│   │   ├── TaskController             异步任务管理
│   │   ├── ProxyHealthController      代理健康查询
│   │   ├── AuditController            审计日志查询（Phase 4 实现）
│   │   └── CrsController              坐标系预设列表
│   ├── domain/
│   │   ├── aggregate/                 Layer, LayerGroup, Source, Feature
│   │   ├── repository/                Layer, LayerGroup, Source, Feature
│   │   └── enums/                     LayerType, SourceType, SourceFormat, ImportStatus, StyleType, CrsPreset
│   ├── infrastructure/
│   │   ├── entity/ mapper/ repository/
│   │   └── importer/                  VectorImporter (接口), CsvImporter, GeoJsonImporter, ShpImporter
│   ├── handler/                       query/ + command/
│   ├── task/                          TileTask, ImportRasterTask, ProxyHealthCheckTask
│   ├── proxy/                         TileProxyService, WmtsCapabilitiesParser
│   ├── tile/                          TileService, TileProjectionService
│   ├── audit/                         AuditService
│   ├── metrics/                       MapMetricsService
│   └── config/                        MapAutoConfiguration
└── src/main/resources/
    ├── application.yml
    └── sql/map_schema.sql
```

### MapAutoConfiguration

- 注册异步切片线程池（CPU 核数 ~ 核数×2）
- 注册代理 OkHttp 客户端（连接 10s / 读取 30s / 最大连接 50）
- 注册定时健康检查调度器（每 5 分钟）
- 注册栅格根目录路径（启动时自动创建目录）

---

## 七、API 设计

### 底图

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/basemap` | 底图列表 |
| `GET` | `/api/v1/map/basemap/default` | 默认底图 |
| `POST` | `/api/v1/map/basemap` | 创建 |
| `PUT` | `/api/v1/map/basemap/{id}` | 更新 |
| `DELETE` | `/api/v1/map/basemap/{id}` | 删除 |

### 图层分组

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/group/tree` | 树形列表 |
| `POST` | `/api/v1/map/group` | 创建 |
| `PUT` | `/api/v1/map/group/{id}` | 更新 |
| `DELETE` | `/api/v1/map/group/{id}` | 删除 |

### 图层

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/layer` | 列表（groupId 筛选） |
| `GET` | `/api/v1/map/layer/{id}` | 详情（含 source 摘要） |
| `POST` | `/api/v1/map/layer` | 创建（target_srid, layer_type） |
| `PUT` | `/api/v1/map/layer/{id}` | 更新 |
| `DELETE` | `/api/v1/map/layer/{id}` | 删除（级联） |

### 数据源

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/layer/{layerId}/source` | 图层数据源列表 |
| `GET` | `/api/v1/map/source/{id}` | 数据源详情 |
| `DELETE` | `/api/v1/map/source/{id}` | 删除数据源（级联） |

### 外部数据库连接

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/datasource` | 连接列表 |
| `GET` | `/api/v1/map/datasource/{id}` | 连接详情 |
| `POST` | `/api/v1/map/datasource` | 创建连接（密码 AES-GCM 加密存储） |
| `PUT` | `/api/v1/map/datasource/{id}` | 更新连接 |
| `DELETE` | `/api/v1/map/datasource/{id}` | 删除连接 |
| `POST` | `/api/v1/map/datasource/{id}/test` | 测试连接 |

### 数据导入

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/v1/map/import?layerId={id}` | 追加矢量到已有图层 |
| `POST` | `/api/v1/map/import` | 导入矢量并新建图层 |
| `POST` | `/api/v1/map/import/postgis` | 注册 PostGIS 外部表 |
| `POST` | `/api/v1/map/import/raster` | API 上传栅格（< 500MB） |
| `POST` | `/api/v1/map/import/raster/register` | 注册磁盘已有栅格 |

### 栅格管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/source/{id}/raster/meta` | 元数据 |
| `GET` | `/api/v1/map/source/{id}/raster/download` | 下载 |
| `POST` | `/api/v1/map/source/{id}/raster/retile` | 重新切片（异步） |

### ZXY 瓦片

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/tile/{sourceId}/{z}/{x}/{y}.png` | 获取瓦片 |
| `GET` | `/api/v1/map/tile/{sourceId}/{z}/{x}/{y}.jpeg` | jpeg 格式 |
| `GET` | `/api/v1/map/tile/{sourceId}/{z}/{x}/{y}.png?projection=EPSG:4490` | 指定投影 |
| `GET` | `/api/v1/map/source/{id}/tile/status` | 切片/代理状态 |

### 要素

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/layer/{layerId}/feature` | 列表（bbox 查询，自动路由 FILE/POSTGIS） |
| `GET` | `/api/v1/map/layer/{layerId}/feature/cluster?k=10&bbox=...` | 聚类 |
| `GET` | `/api/v1/map/layer/{layerId}/feature/geojson` | GeoJSON 格式 |
| `GET` | `/api/v1/map/feature/{id}` | 详情 |
| `POST` | `/api/v1/map/layer/{layerId}/feature` | 新增（仅 FILE） |
| `PUT` | `/api/v1/map/feature/{id}` | 更新（仅 FILE） |
| `DELETE` | `/api/v1/map/feature/{id}` | 删除（仅 FILE） |

**PostGIS 直查路由：** source_type=POSTGIS 时通过 dynamic-datasource 切换到 external_db_key，动态 SQL 查外部表。

### 样式 / 导出 / 坐标系

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET/POST` | `/api/v1/map/layer/{layerId}/style` | 样式列表/创建 |
| `PUT/DELETE` | `/api/v1/map/style/{id}` | 更新/删除 |
| `GET` | `/api/v1/map/layer/{layerId}/export?format=geojson` | 导出（支持 sourceId 筛选） |
| `GET` | `/api/v1/map/crs/presets` | 常用坐标系预设（硬编码枚举） |

### 地图实例与发布

**图层树请求体格式（PUT /instance/{id}/layers）：**

```json
[
  {
    "name": "底图数据",
    "isGroup": true,
    "visible": true,
    "children": [
      { "layerId": "xxx", "name": "卫星影像", "visible": true, "opacity": 1.0 },
      { "layerId": "yyy", "name": "路网", "visible": true }
    ]
  },
  {
    "name": "业务数据",
    "isGroup": true,
    "children": [
      { "layerId": "zzz", "name": "建筑" }
    ]
  }
]
```

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET/POST` | `/api/v1/map/instance` | 实例列表/创建 |
| `GET/PUT/DELETE` | `/api/v1/map/instance/{id}` | 详情/更新/删除 |
| `GET` | `/api/v1/map/instance/{id}/layers` | 图层树（返回嵌套结构） |
| `PUT` | `/api/v1/map/instance/{id}/layers` | 更新图层树（整体替换，支持分组嵌套） |
| `GET/POST` | `/api/v1/map/instance/{id}/publish` | 发布列表/创建 |
| `PUT/DELETE` | `/api/v1/map/publish/{id}` | 更新/删除发布 |
| `GET` | `/api/v1/map/public/{publishId}` | 公开访问入口 |
| `GET` | `/api/v1/map/public/{publishId}/config` | 公开地图配置 |
| `GET` | `/api/v1/map/public/{publishId}/tile/{sourceId}/{z}/{x}/{y}.png` | 公开瓦片 |

### 版本 / 任务 / 健康

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/map/layer/{layerId}/version` | 版本列表 |
| `GET` | `/api/v1/map/layer/{layerId}/version/{version}` | 版本详情 |
| `POST` | `/api/v1/map/layer/{layerId}/version` | 手动快照 |
| `POST` | `/api/v1/map/layer/{layerId}/rollback/{version}` | 回滚 |
| `GET` | `/api/v1/map/task` | 任务列表 |
| `GET` | `/api/v1/map/task/{id}` | 任务详情 |
| `POST` | `/api/v1/map/task/{id}/cancel` | 取消任务 |
| `GET` | `/api/v1/map/source/{id}/proxy/health` | 代理健康 |
| `POST` | `/api/v1/map/source/{id}/proxy/check` | 手动检查 |

---

## 八、技术依赖

| 能力 | 依赖 |
|------|------|
| 矢量解析 | `zyn-infra-geo`（GeoTools、ShpReader/Writer） |
| 栅格存储 | `zyn-infra-storage`（StorageService） |
| 空间查询 | PostgreSQL PostGIS（ST_Intersects / ST_Within / ST_Buffer / ST_ClusterKMeans） |
| 坐标转换 | `zyn-infra-geo` GeoCrsUtils |
| 数据库 | PostgreSQL + PostGIS，GEOMETRY 列 |
| 认证 | Sa-Token |
| 配置 | `zyn-conf` |
| HTTP 代理 | OkHttp（`zyn-kit` HttpClient） |
| 异步任务 | Spring @Async + `zyn-kit` ThreadPoolBuilder |
| ID 生成 | `zyn-kit` IdUtils.snowflakeId（map_feature BIGINT ID） |
| 加密 | AES-GCM（`zyn-kit` AesUtils） |
| 可观测性 | Micrometer + Prometheus（Actuator） |
| 多数据源 | dynamic-datasource（PostGIS 直查） |

---

## 九、可观测性指标

> Phase 4 通过 Micrometer 暴露 Prometheus 指标

| 指标 | 类型 | 标签 | 说明 |
|------|------|------|------|
| `map_tile_request_total` | Counter | source_type, projection, hit/miss | 瓦片请求总数 |
| `map_tile_cache_hit_rate` | Gauge | — | 缓存命中率 |
| `map_tile_latency_seconds` | Histogram | source_type, projection | 响应延迟 |
| `map_proxy_request_total` | Counter | source_type, status_code | 代理请求总数 |
| `map_proxy_fail_rate` | Gauge | source_id | 代理失败率 |
| `map_import_duration_seconds` | Histogram | source_format, result | 导入耗时 |
| `map_import_feature_count` | Counter | source_format | 导入要素总数 |
| `map_feature_query_duration_seconds` | Histogram | query_type | 查询延迟 |
| `map_async_task_duration_seconds` | Histogram | task_type | 异步任务耗时 |

Grafana 面板：瓦片 QPS + 命中率 + P99、代理健康状态、导入统计、慢查询 Top10。

---

## 十、实施阶段

### Phase 1 — 基础管理（1-2 周）

- [ ] 底图 CRUD（XYZ/WMS 配置，默认底图）
- [ ] 分组 CRUD + 树形接口
- [ ] 图层 CRUD（target_srid、render_order）
- [ ] 字段管理 + 样式 CRUD
- [ ] 数据源列表 / 删除
- [ ] 操作审计日志
- [ ] 坐标系预设 API

### Phase 2 — 矢量导入（1-2 周）

- [ ] GeoJSON / CSV / Shapefile 导入 + 坐标转换
- [ ] 追加导入（source_srid 可不同）
- [ ] 导入校验（ST_IsValid、空几何、坐标范围）
- [ ] 导入前自动生成图层版本快照
- [ ] 栅格文件上传/下载

### Phase 3 — 空间查询（1 周）

- [ ] 要素 CRUD
- [ ] bbox / 点 / 多边形空间查询
- [ ] 属性查询（JSONB 条件）
- [ ] 按 source_id 筛选
- [ ] 要素分页（游标/流式）
- [ ] 数据导出（GeoJSON / Shapefile / CSV）

### Phase 4 — 增强（按需）

- [ ] 矢量瓦片（MVT） + 栅格切片
- [ ] 要素聚类查询
- [ ] PostGIS 直查模式
- [ ] 代理健康监控 + 降级
- [ ] 地图实例 + 图层叠加
- [ ] 地图发布 + 公开访问
- [ ] 图层版本回滚
- [ ] 图层权限 + 公开/私有
- [ ] 空间分析（缓冲区、叠加）
- [ ] Prometheus 指标 + Grafana Dashboard
