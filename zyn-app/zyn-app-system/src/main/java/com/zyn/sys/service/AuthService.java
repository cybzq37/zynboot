package com.zyn.sys.service;

import com.zyn.api.sys.response.user.LoginRes;
import com.zyn.api.sys.response.user.UserInfoRes;

public interface AuthService {

    LoginRes login(String username, String password);

    UserInfoRes getCurrentUserInfo();
}
