package com.zyn.sys.command.org;

import lombok.Builder;
import lombok.Value;

/**
 * 组织保存命令（创建 / 更新）。
 * <p>
 * id 为空时创建，id 非空时更新。
 */
@Value
@Builder
public class OrgSaveCmd {

    /** 组织 ID（更新时必填，创建时为空）。 */
    String id;

    /** 父级 ID。 */
    String parentId;

    /** 组织编码。 */
    String orgCode;

    /** 组织名称。 */
    String orgName;

    /** 组织类型：1=总公司 2=分公司 3=部门 4=小组。 */
    Integer orgType;

    /** 负责人 ID。 */
    String leaderId;

    /** 联系电话。 */
    String phone;

    /** 邮箱。 */
    String email;

    /** 排序。 */
    Integer sort;

    /** 状态：0=禁用 1=启用。 */
    Integer status;

    /** 备注。 */
    String remark;
}
