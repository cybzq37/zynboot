package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysOrganization;

/**
 * 组织聚合根。
 */
public class OrgAggregate {

    private final SysOrganization entity;

    private OrgAggregate(SysOrganization entity) {
        this.entity = entity;
    }

    public static OrgAggregate from(SysOrganization entity) {
        return new OrgAggregate(entity);
    }

    public static OrgAggregate create(String code, String name, Integer type) {
        SysOrganization org = new SysOrganization();
        org.setCode(code);
        org.setName(name);
        org.setType(type);
        org.setStatus(1);
        return new OrgAggregate(org);
    }

    /** 供 Repository 层持久化使用。 */
    public SysOrganization getEntity() {
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

    public Integer getType() {
        return entity.getType();
    }

    public Integer getSortOrder() {
        return entity.getSortOrder();
    }

    public Integer getStatus() {
        return entity.getStatus();
    }

    public void setParentId(String parentId) {
        entity.setParentId(parentId);
    }

    public void updateInfo(String name, String phone, String email, String remark) {
        if (name != null) entity.setName(name);
        if (phone != null) entity.setPhone(phone);
        if (email != null) entity.setEmail(email);
        if (remark != null) entity.setRemark(remark);
    }
}
