package com.zyn.sys.controller;

import com.zyn.sys.response.user.LoginUserRes;
import com.zyn.sys.response.user.UserRes;
import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.UserInfoRes;
import cn.dev33.satoken.stp.StpUtil;
import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.api.SysAuthApi;
import com.zyn.sys.handler.command.AuthCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements SysAuthApi {

    private final AuthCommandHandler authService;

    @Override
    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@RequestParam String username, @RequestParam String password) {
        LoginRes loginVO = authService.login(username, password);
        return ApiResponse.ok(loginVO);
    }

    @Override
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok(null);
    }

    @Override
    @GetMapping("/info")
    public ApiResponse<UserInfoRes> info() {
        UserInfoRes userInfo = authService.getCurrentUserInfo();
        return ApiResponse.ok(userInfo);
    }
}
