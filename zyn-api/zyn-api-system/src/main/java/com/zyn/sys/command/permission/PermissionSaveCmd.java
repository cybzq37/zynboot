package com.zyn.sys.command.permission;

import lombok.Builder;
import lombok.Value;

/**
 * 权限保存命令（创建 / 更新）。
 * <p>
 * id 为空时创建，id 非空时更新。
 */
@Value
@Builder
public class PermissionSaveCmd {

    /** 权限 ID（更新时必填，创建时为空）。 */
    String id;

    /** 父级 ID。 */
    String parentId;

    /** 权限编码。 */
    String permCode;

    /** 权限名称。 */
    String permName;

    /** 权限类型：1=目录 2=菜单 3=按钮 4=API。 */
    Integer permType;

    /** 路由路径。 */
    String path;

    /** 排序。 */
    Integer sort;

    /** 是否可见。 */
    Boolean visible;

    /** 状态：0=禁用 1=启用。 */
    Integer status;

    /** 备注。 */
    String remark;
}
