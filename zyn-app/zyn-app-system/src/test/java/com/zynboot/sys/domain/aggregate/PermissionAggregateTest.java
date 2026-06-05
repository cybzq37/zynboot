package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysPermission;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionAggregateTest {

    @Test
    void shouldCreatePermissionWithDefaults() {
        PermissionAggregate perm = PermissionAggregate.create(null, "sys:user", "用户管理", 2);

        assertThat(perm.getParentId()).isNull();
        assertThat(perm.getCode()).isEqualTo("sys:user");
        assertThat(perm.getName()).isEqualTo("用户管理");
        assertThat(perm.getType()).isEqualTo(2);
        assertThat(perm.getStatus()).isEqualTo(1);
        assertThat(perm.getVisible()).isTrue();
    }

    @Test
    void shouldReconstituteFromEntity() {
        SysPermission entity = new SysPermission();
        entity.setId("p-001");
        entity.setCode("sys:role");
        entity.setName("角色管理");

        PermissionAggregate perm = PermissionAggregate.from(entity);

        assertThat(perm.getId()).isEqualTo("p-001");
        assertThat(perm.getCode()).isEqualTo("sys:role");
    }

    @Test
    void shouldUpdateInfoWithNonNullFieldsOnly() {
        PermissionAggregate perm = PermissionAggregate.create(null, "sys:user", "用户管理", 2);
        perm.updateInfo("用户管理V2", "/system/user", 1, true, 1, "updated");

        assertThat(perm.getName()).isEqualTo("用户管理V2");
        assertThat(perm.getPath()).isEqualTo("/system/user");
        assertThat(perm.getSortOrder()).isEqualTo(1);
        assertThat(perm.getRemark()).isEqualTo("updated");
    }

    @Test
    void shouldNotOverwriteWithNullOnUpdate() {
        PermissionAggregate perm = PermissionAggregate.create(null, "sys:user", "用户管理", 2);
        perm.updateInfo("用户管理V2", "/system/user", 1, true, 1, "note");
        perm.updateInfo(null, null, null, false, null, null);

        assertThat(perm.getName()).isEqualTo("用户管理V2");
        assertThat(perm.getPath()).isEqualTo("/system/user");
        assertThat(perm.getSortOrder()).isEqualTo(1);
        assertThat(perm.getVisible()).isFalse();
        assertThat(perm.getRemark()).isEqualTo("note");
    }

    @Test
    void shouldExposeEntityForPersistence() {
        PermissionAggregate perm = PermissionAggregate.create(null, "sys:user", "用户管理", 2);
        SysPermission entity = perm.getEntity();

        assertThat(entity).isNotNull();
        assertThat(entity.getCode()).isEqualTo("sys:user");
    }
}
