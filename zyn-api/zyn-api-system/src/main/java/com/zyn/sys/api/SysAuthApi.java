package com.zyn.sys.api;

import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.UserInfoRes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@ServiceClient("sys")
@HttpExchange("/api/v1/auth")
public interface SysAuthApi {

    @PostExchange("/login")
    ApiResponse<LoginRes> login(@RequestParam String username, @RequestParam String password);

    @PostExchange("/logout")
    ApiResponse<Void> logout();

    @GetExchange("/info")
    ApiResponse<UserInfoRes> info();
}
