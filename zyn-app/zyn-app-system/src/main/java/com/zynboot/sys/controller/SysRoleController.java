package com.zynboot.sys.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.BeanUtils;
import com.zynboot.sys.command.role.RoleSaveCmd;
import com.zynboot.sys.domain.aggregate.RoleAggregate;
import com.zynboot.sys.domain.repository.RoleRepository;
import com.zynboot.sys.handler.query.PermissionQueryHandler;
import com.zynboot.sys.handler.query.RoleQueryHandler;
import com.zynboot.sys.query.role.RoleQuery;
import com.zynboot.sys.response.role.RoleRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class SysRoleController {

    private final RoleQueryHandler roleQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;
    private final RoleRepository roleRepository;

    @GetMapping
    public ApiResponse<List<RoleRes>> list(RoleQuery query) {
        return ApiResponse.ok(BeanUtils.copyList(roleRepository.findList(query), RoleRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleRes> getById(@PathVariable String id) {
        return ApiResponse.ok(roleQueryHandler.findById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody RoleSaveCmd cmd) {
        RoleAggregate role = RoleAggregate.create(cmd.getCode(), cmd.getName());
        role.updateInfo(cmd.getName(), cmd.getDataScope(), cmd.getRemark());
        roleRepository.save(role);
        permissionQueryHandler.clearCacheByRoleId(role.getId());
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody RoleSaveCmd cmd) {
        RoleAggregate role = roleRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("角色"));
        role.updateInfo(cmd.getName(), cmd.getDataScope(), cmd.getRemark());
        roleRepository.update(role);
        permissionQueryHandler.clearCacheByRoleId(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionQueryHandler.clearCacheByRoleId(id);
        roleRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
