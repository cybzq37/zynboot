package com.zyn.sys.domain.aggregate;

import com.zyn.sys.infrastructure.entity.SysOrganization;
import lombok.Getter;

/**
 * 组织聚合根。
 */
@Getter
public class OrgAggregate {

    private final SysOrganization entity;

    private OrgAggregate(SysOrganization entity) {
        this.entity = entity;
    }

    public static OrgAggregate from(SysOrganization entity) {
        return new OrgAggregate(entity);
    }

    public static OrgAggregate create(String orgCode, String orgName, Integer orgType) {
        SysOrganization org = new SysOrganization();
        org.setOrgCode(orgCode);
        org.setOrgName(orgName);
        org.setOrgType(orgType);
        org.setStatus(1);
        return new OrgAggregate(org);
    }

    public void updateInfo(String orgName, String phone, String email, String remark) {
        if (orgName != null) entity.setOrgName(orgName);
        if (phone != null) entity.setPhone(phone);
        if (email != null) entity.setEmail(email);
        if (remark != null) entity.setRemark(remark);
    }
}
