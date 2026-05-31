package com.zyn.app.controller;

import com.zyn.api.sys.client.RemoteUserService;
import com.zyn.api.sys.response.user.UserRes;
import com.zyn.api.sys.response.user.UserInfoRes;
import com.zyn.kit.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sys-demo")
public class SysDemoController {

    private final RemoteUserService remoteUserService;

    @GetMapping("/user/{id}")
    public ApiResponse<UserRes> getUser(@PathVariable String id) {
        return remoteUserService.getById(id);
    }

    @GetMapping("/user/info")
    public ApiResponse<UserInfoRes> getUserInfo() {
        return remoteUserService.getUserInfo();
    }
}
