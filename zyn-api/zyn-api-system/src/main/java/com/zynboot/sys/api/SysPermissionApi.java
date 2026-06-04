package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.permission.PermissionSaveCmd;
import com.zynboot.sys.query.permission.PermissionQuery;
import com.zynboot.sys.response.permission.MenuTreeRes;
import com.zynboot.sys.response.permission.PermissionRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "sys", path = "/api/v1/permission")
public interface SysPermissionApi {

    @GetMapping
    ApiResponse<List<PermissionRes>> list(@SpringQueryMap PermissionQuery query);

    @GetMapping("/tree")
    ApiResponse<List<MenuTreeRes>> tree();

    @GetMapping("/{id}")
    ApiResponse<PermissionRes> getById(@PathVariable String id);

    @PostMapping
    ApiResponse<Void> create(@RequestBody PermissionSaveCmd cmd);

    @PutMapping("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody PermissionSaveCmd cmd);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
