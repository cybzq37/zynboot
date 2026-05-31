package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.user.UserSaveCmd;
import com.zyn.sys.query.user.UserPageQuery;
import com.zyn.sys.response.user.UserRes;

import java.util.Map;

/**
 * 用户管理 API。
 */
public interface SysUserApi {

    /**
     * 分页查询用户。
     */
    ApiResponse<Map<String, Object>> page(UserPageQuery query);

    /**
     * 根据 ID 获取用户。
     */
    ApiResponse<UserRes> getById(String id);

    /**
     * 创建用户。
     */
    ApiResponse<Void> create(UserSaveCmd cmd);

    /**
     * 更新用户。
     */
    ApiResponse<Void> update(String id, UserSaveCmd cmd);

    /**
     * 删除用户。
     */
    ApiResponse<Void> delete(String id);
}
