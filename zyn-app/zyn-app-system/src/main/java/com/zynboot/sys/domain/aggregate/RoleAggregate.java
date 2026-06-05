package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysRole;

/**
 * 角色聚合根。
 */
public class RoleAggregate {

    private final SysRole entity;

    private RoleAggregate(SysRole entity) {
        this.entity = entity;
    }

    public static RoleAggregate from(SysRole entity) {
        return new RoleAggregate(entity);
    }

    public static RoleAggregate create(String code, String name) {
        SysRole role = new SysRole();
        role.setCode(code);
        role.setName(name);
        role.setStatus(1);
        return new RoleAggregate(role);
    }

    /** 供 Repository 层持久化使用。 */
    public SysRole getEntity() {
        return entity;
    }

    public String getId() {
        return entity.getId();
    }

    public String getCode() {
        return entity.getCode();
    }

    public String getName() {
        return entity.getName();
    }

    public Integer getDataScope() {
        return entity.getDataScope();
    }

    public Integer getSortOrder() {
        return entity.getSortOrder();
    }

    public Integer getStatus() {
        return entity.getStatus();
    }

    public String getRemark() {
        return entity.getRemark();
    }

    public void updateInfo(String name, Integer dataScope, String remark) {
        if (name != null) entity.setName(name);
        if (dataScope != null) entity.setDataScope(dataScope);
        if (remark != null) entity.setRemark(remark);
    }
}
