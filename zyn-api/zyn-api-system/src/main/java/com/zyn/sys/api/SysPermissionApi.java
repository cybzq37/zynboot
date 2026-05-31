package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.permission.PermissionSaveCmd;
import com.zyn.sys.query.permission.PermissionQuery;
import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.permission.PermissionRes;

import java.util.List;

/**
 * 权限管理 API。
 */
public interface SysPermissionApi {

    /**
     * 查询权限列表。
     */
    ApiResponse<List<PermissionRes>> list(PermissionQuery query);

    /**
     * 获取权限树。
     */
    ApiResponse<List<MenuTreeRes>> tree();

    /**
     * 根据 ID 获取权限。
     */
    ApiResponse<PermissionRes> getById(String id);

    /**
     * 创建权限。
     */
    ApiResponse<Void> create(PermissionSaveCmd cmd);

    /**
     * 更新权限。
     */
    ApiResponse<Void> update(String id, PermissionSaveCmd cmd);

    /**
     * 删除权限。
     */
    ApiResponse<Void> delete(String id);
}
