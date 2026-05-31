package com.zyn.sys.handler.command;

import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.UserInfoRes;

public interface AuthCommandHandler {

    LoginRes login(String username, String password);

    UserInfoRes getCurrentUserInfo();
}
