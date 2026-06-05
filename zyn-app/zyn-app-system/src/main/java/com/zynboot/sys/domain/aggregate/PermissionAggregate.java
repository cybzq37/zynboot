package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysPermission;

/**
 * 权限聚合根，封装权限相关的业务规则。
 */
public class PermissionAggregate {

    private final SysPermission entity;

    private PermissionAggregate(SysPermission entity) {
        this.entity = entity;
    }

    public static PermissionAggregate from(SysPermission entity) {
        return new PermissionAggregate(entity);
    }

    public static PermissionAggregate create(String parentId, String code, String name, Integer type) {
        SysPermission permission = new SysPermission();
        permission.setParentId(parentId);
        permission.setCode(code);
        permission.setName(name);
        permission.setType(type);
        permission.setStatus(1);
        permission.setVisible(true);
        return new PermissionAggregate(permission);
    }

    public SysPermission getEntity() {
        return entity;
    }

    public String getId() {
        return entity.getId();
    }

    public String getParentId() {
        return entity.getParentId();
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

    public String getPath() {
        return entity.getPath();
    }

    public Integer getSortOrder() {
        return entity.getSortOrder();
    }

    public Boolean getVisible() {
        return entity.getVisible();
    }

    public Integer getStatus() {
        return entity.getStatus();
    }

    public String getRemark() {
        return entity.getRemark();
    }

    public void updateInfo(String name, String path, Integer sortOrder, Boolean visible, Integer status, String remark) {
        if (name != null) entity.setName(name);
        if (path != null) entity.setPath(path);
        if (sortOrder != null) entity.setSortOrder(sortOrder);
        if (visible != null) entity.setVisible(visible);
        if (status != null) entity.setStatus(status);
        if (remark != null) entity.setRemark(remark);
    }
}
