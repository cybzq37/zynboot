package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysResource;

/**
 * 资源聚合根，封装资源相关的业务规则。
 */
public class ResourceAggregate {

    private final SysResource entity;

    private ResourceAggregate(SysResource entity) {
        this.entity = entity;
    }

    public static ResourceAggregate from(SysResource entity) {
        return new ResourceAggregate(entity);
    }

    public static ResourceAggregate create(String permissionId, String name, Integer type,
                                           String requestMethod, String requestPath) {
        SysResource resource = new SysResource();
        resource.setPermissionId(permissionId);
        resource.setName(name);
        resource.setType(type);
        resource.setRequestMethod(requestMethod);
        resource.setRequestPath(requestPath);
        resource.setStatus(1);
        return new ResourceAggregate(resource);
    }

    public SysResource getEntity() {
        return entity;
    }

    public String getId() {
        return entity.getId();
    }

    public String getPermissionId() {
        return entity.getPermissionId();
    }

    public String getName() {
        return entity.getName();
    }

    public Integer getType() {
        return entity.getType();
    }

    public String getRequestMethod() {
        return entity.getRequestMethod();
    }

    public String getRequestPath() {
        return entity.getRequestPath();
    }

    public Integer getStatus() {
        return entity.getStatus();
    }

    public String getRemark() {
        return entity.getRemark();
    }

    public void updateInfo(String name, String requestMethod, String requestPath, Integer status, String remark) {
        if (name != null) entity.setName(name);
        if (requestMethod != null) entity.setRequestMethod(requestMethod);
        if (requestPath != null) entity.setRequestPath(requestPath);
        if (status != null) entity.setStatus(status);
        if (remark != null) entity.setRemark(remark);
    }
}
