package com.zyn.sys.command.role;

import lombok.Builder;
import lombok.Value;

/**
 * 角色保存命令（创建 / 更新）。
 * <p>
 * id 为空时创建，id 非空时更新。
 */
@Value
@Builder
public class RoleSaveCmd {

    /** 角色 ID（更新时必填，创建时为空）。 */
    String id;

    /** 角色编码。 */
    String roleCode;

    /** 角色名称。 */
    String roleName;

    /** 数据范围：1=全部 2=本部门及子部门 3=本部门 4=仅本人。 */
    Integer dataScope;

    /** 排序。 */
    Integer sort;

    /** 状态：0=禁用 1=启用。 */
    Integer status;

    /** 备注。 */
    String remark;
}
