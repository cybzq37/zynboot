package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.role.RoleSaveCmd;
import com.zyn.sys.query.role.RoleQuery;
import com.zyn.sys.response.role.RoleRes;

import java.util.List;

/**
 * 角色管理 API。
 */
public interface SysRoleApi {

    /**
     * 查询角色列表。
     */
    ApiResponse<List<RoleRes>> list(RoleQuery query);

    /**
     * 根据 ID 获取角色。
     */
    ApiResponse<RoleRes> getById(String id);

    /**
     * 创建角色。
     */
    ApiResponse<Void> create(RoleSaveCmd cmd);

    /**
     * 更新角色。
     */
    ApiResponse<Void> update(String id, RoleSaveCmd cmd);

    /**
     * 删除角色。
     */
    ApiResponse<Void> delete(String id);
}
