package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.user.LoginCmd;
import com.zyn.sys.handler.command.AuthCommandHandler;
import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.UserInfoRes;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthCommandHandler authService;

    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@RequestBody LoginCmd cmd) {
        return ApiResponse.ok(authService.login(cmd.getUsername(), cmd.getPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok(null);
    }

    @GetMapping("/info")
    public ApiResponse<UserInfoRes> info() {
        return ApiResponse.ok(authService.getCurrentUserInfo());
    }
}
