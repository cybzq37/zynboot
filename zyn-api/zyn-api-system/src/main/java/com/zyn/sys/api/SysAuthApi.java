package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.UserInfoRes;

/**
 * 认证 API。
 */
public interface SysAuthApi {

    /**
     * 登录。
     */
    ApiResponse<LoginRes> login(String username, String password);

    /**
     * 登出。
     */
    ApiResponse<Void> logout();

    /**
     * 获取当前登录用户信息。
     */
    ApiResponse<UserInfoRes> info();
}
