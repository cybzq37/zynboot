package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.user.LoginCmd;
import com.zynboot.sys.response.user.LoginRes;
import com.zynboot.sys.response.user.UserInfoRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "zyn-sys", contextId = "SysAuthApi", path = "/auth")
public interface SysAuthApi {

    @PostMapping("/login")
    ApiResponse<LoginRes> login(@RequestBody LoginCmd cmd);

    @PostMapping("/logout")
    ApiResponse<Void> logout();

    @GetMapping("/info")
    ApiResponse<UserInfoRes> info();
}
