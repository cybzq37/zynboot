package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysOrganization;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrgAggregateTest {

    @Test
    void shouldCreateOrgWithDefaults() {
        OrgAggregate org = OrgAggregate.create("TECH", "技术部", 1);

        assertThat(org.getCode()).isEqualTo("TECH");
        assertThat(org.getName()).isEqualTo("技术部");
        assertThat(org.getType()).isEqualTo(1);
        assertThat(org.getStatus()).isEqualTo(1);
    }

    @Test
    void shouldReconstituteFromEntity() {
        SysOrganization entity = new SysOrganization();
        entity.setId("o-001");
        entity.setCode("HR");

        OrgAggregate org = OrgAggregate.from(entity);

        assertThat(org.getId()).isEqualTo("o-001");
        assertThat(org.getCode()).isEqualTo("HR");
    }

    @Test
    void shouldSetParentId() {
        OrgAggregate org = OrgAggregate.create("CHILD", "子部门", 1);

        org.setParentId("parent-001");

        assertThat(org.getEntity().getParentId()).isEqualTo("parent-001");
    }

    @Test
    void shouldUpdateInfoWithNonNullFieldsOnly() {
        OrgAggregate org = OrgAggregate.create("TECH", "技术部", 1);

        org.updateInfo("研发中心", "123456", "a@b.com", "备注");

        assertThat(org.getName()).isEqualTo("研发中心");
        assertThat(org.getEntity().getPhone()).isEqualTo("123456");
        assertThat(org.getEntity().getEmail()).isEqualTo("a@b.com");
        assertThat(org.getEntity().getRemark()).isEqualTo("备注");
    }

    @Test
    void shouldNotOverwriteWithNullOnUpdate() {
        OrgAggregate org = OrgAggregate.create("TECH", "技术部", 1);
        org.updateInfo("研发中心", "123456", "a@b.com", "备注");

        org.updateInfo("新名称", null, null, "新备注");

        assertThat(org.getName()).isEqualTo("新名称");
        assertThat(org.getEntity().getPhone()).isEqualTo("123456");
        assertThat(org.getEntity().getEmail()).isEqualTo("a@b.com");
        assertThat(org.getEntity().getRemark()).isEqualTo("新备注");
    }
}
