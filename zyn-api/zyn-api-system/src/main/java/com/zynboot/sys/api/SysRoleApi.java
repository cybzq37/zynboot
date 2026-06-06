package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.role.RoleSaveCmd;
import com.zynboot.sys.query.role.RoleQuery;
import com.zynboot.sys.response.role.RoleRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "zyn-sys", contextId = "SysRoleApi", path = "/role")
public interface SysRoleApi {

    @GetMapping
    ApiResponse<List<RoleRes>> list(@SpringQueryMap RoleQuery query);

    @GetMapping("/{id}")
    ApiResponse<RoleRes> getById(@PathVariable String id);

    @PostMapping
    ApiResponse<Void> create(@RequestBody RoleSaveCmd cmd);

    @PutMapping("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody RoleSaveCmd cmd);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
