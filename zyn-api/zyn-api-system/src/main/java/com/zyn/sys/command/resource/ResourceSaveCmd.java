package com.zyn.sys.command.resource;

import lombok.Builder;
import lombok.Value;

/**
 * API 资源保存命令（创建 / 更新）。
 * <p>
 * id 为空时创建，id 非空时更新。
 */
@Value
@Builder
public class ResourceSaveCmd {

    /** 资源 ID（更新时必填，创建时为空）。 */
    String id;

    /** 关联权限 ID。 */
    String permissionId;

    /** 资源名称。 */
    String resName;

    /** 资源类型：1=API 2=文件 3=数据。 */
    Integer resType;

    /** 请求方法（GET/POST/PUT/DELETE）。 */
    String requestMethod;

    /** 请求路径。 */
    String requestPath;

    /** 状态：0=禁用 1=启用。 */
    Integer status;

    /** 备注。 */
    String remark;
}
