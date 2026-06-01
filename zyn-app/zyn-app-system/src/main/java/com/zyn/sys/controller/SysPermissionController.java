package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.permission.PermissionSaveCmd;
import com.zyn.sys.domain.repository.PermissionRepository;
import com.zyn.sys.handler.query.PermissionQueryHandler;
import com.zyn.sys.infrastructure.entity.SysPermission;
import com.zyn.sys.query.permission.PermissionQuery;
import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.permission.PermissionRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permission")
public class SysPermissionController {

    private final PermissionQueryHandler permissionQueryHandler;
    private final PermissionRepository permissionRepository;

    @GetMapping
    public ApiResponse<List<PermissionRes>> list(PermissionQuery query) {
        return ApiResponse.ok(BeanUtils.copyList(permissionRepository.findList(query), PermissionRes.class));
    }

    @GetMapping("/tree")
    public ApiResponse<List<MenuTreeRes>> tree() {
        return ApiResponse.ok(permissionQueryHandler.getPermissionTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionRes> getById(@PathVariable String id) {
        SysPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在"));
        return ApiResponse.ok(BeanUtils.copy(permission, PermissionRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody PermissionSaveCmd cmd) {
        SysPermission permission = BeanUtils.copy(cmd, SysPermission.class);
        permissionRepository.save(permission);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody PermissionSaveCmd cmd) {
        SysPermission permission = BeanUtils.copy(cmd, SysPermission.class);
        permission.setId(id);
        permissionRepository.update(permission);
        permissionQueryHandler.clearCacheByPermissionId(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionQueryHandler.clearCacheByPermissionId(id);
        permissionRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
