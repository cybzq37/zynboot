package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.user.UserSaveCmd;
import com.zynboot.sys.query.user.UserPageQuery;
import com.zynboot.sys.response.PageRes;
import com.zynboot.sys.response.user.UserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "sys", contextId = "SysUserApi", path = "/api/v1/user")
public interface SysUserApi {

    @GetMapping
    ApiResponse<PageRes<UserRes>> page(@SpringQueryMap UserPageQuery query);

    @GetMapping("/{id}")
    ApiResponse<UserRes> getById(@PathVariable String id);

    @PostMapping
    ApiResponse<Void> create(@RequestBody UserSaveCmd cmd);

    @PutMapping("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody UserSaveCmd cmd);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
