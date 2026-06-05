package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceAggregateTest {

    @Test
    void shouldCreateResourceWithDefaults() {
        ResourceAggregate res = ResourceAggregate.create("perm-001", "用户查询", 1, "GET", "/api/v1/user");

        assertThat(res.getPermissionId()).isEqualTo("perm-001");
        assertThat(res.getName()).isEqualTo("用户查询");
        assertThat(res.getType()).isEqualTo(1);
        assertThat(res.getRequestMethod()).isEqualTo("GET");
        assertThat(res.getRequestPath()).isEqualTo("/api/v1/user");
        assertThat(res.getStatus()).isEqualTo(1);
    }

    @Test
    void shouldReconstituteFromEntity() {
        SysResource entity = new SysResource();
        entity.setId("r-001");
        entity.setName("角色创建");
        entity.setRequestMethod("POST");

        ResourceAggregate res = ResourceAggregate.from(entity);

        assertThat(res.getId()).isEqualTo("r-001");
        assertThat(res.getName()).isEqualTo("角色创建");
    }

    @Test
    void shouldUpdateInfoWithNonNullFieldsOnly() {
        ResourceAggregate res = ResourceAggregate.create("perm-001", "用户查询", 1, "GET", "/api/v1/user");
        res.updateInfo("用户列表查询", "POST", "/api/v1/user/list", 1, "updated");

        assertThat(res.getName()).isEqualTo("用户列表查询");
        assertThat(res.getRequestMethod()).isEqualTo("POST");
        assertThat(res.getRequestPath()).isEqualTo("/api/v1/user/list");
        assertThat(res.getRemark()).isEqualTo("updated");
    }

    @Test
    void shouldNotOverwriteWithNullOnUpdate() {
        ResourceAggregate res = ResourceAggregate.create("perm-001", "用户查询", 1, "GET", "/api/v1/user");
        res.updateInfo("用户列表查询", "POST", "/api/v1/user/list", 1, "note");
        res.updateInfo(null, null, null, 0, null);

        assertThat(res.getName()).isEqualTo("用户列表查询");
        assertThat(res.getRequestMethod()).isEqualTo("POST");
        assertThat(res.getStatus()).isEqualTo(0);
        assertThat(res.getRemark()).isEqualTo("note");
    }

    @Test
    void shouldExposeEntityForPersistence() {
        ResourceAggregate res = ResourceAggregate.create("perm-001", "用户查询", 1, "GET", "/api/v1/user");
        SysResource entity = res.getEntity();

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("用户查询");
    }
}
