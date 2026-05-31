package com.zyn.sys.domain.aggregate;

import com.zyn.sys.infrastructure.entity.SysRole;
import lombok.Getter;

/**
 * 角色聚合根。
 */
@Getter
public class RoleAggregate {

    private final SysRole entity;

    private RoleAggregate(SysRole entity) {
        this.entity = entity;
    }

    public static RoleAggregate from(SysRole entity) {
        return new RoleAggregate(entity);
    }

    public static RoleAggregate create(String roleCode, String roleName) {
        SysRole role = new SysRole();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setStatus(1);
        return new RoleAggregate(role);
    }

    public void updateInfo(String roleName, Integer dataScope, String remark) {
        if (roleName != null) entity.setRoleName(roleName);
        if (dataScope != null) entity.setDataScope(dataScope);
        if (remark != null) entity.setRemark(remark);
    }
}
