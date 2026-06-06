package com.zynboot.sys.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.BeanUtils;
import com.zynboot.sys.command.permission.PermissionSaveCmd;
import com.zynboot.sys.domain.aggregate.PermissionAggregate;
import com.zynboot.sys.domain.repository.PermissionRepository;
import com.zynboot.sys.handler.query.PermissionQueryHandler;
import com.zynboot.sys.query.permission.PermissionQuery;
import com.zynboot.sys.response.permission.MenuTreeRes;
import com.zynboot.sys.response.permission.PermissionRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.zynboot.infra.web.version.ApiVersion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/permission")
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
        PermissionAggregate permission = permissionRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("权限"));
        return ApiResponse.ok(BeanUtils.copy(permission.getEntity(), PermissionRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody PermissionSaveCmd cmd) {
        PermissionAggregate permission = PermissionAggregate.create(
                cmd.getParentId(), cmd.getCode(), cmd.getName(), cmd.getType());
        permission.updateInfo(cmd.getName(), cmd.getPath(), cmd.getSortOrder(),
                cmd.getVisible(), cmd.getStatus(), cmd.getRemark());
        permissionRepository.save(permission);
        permissionQueryHandler.clearCacheByPermissionId(permission.getId());
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody PermissionSaveCmd cmd) {
        PermissionAggregate permission = permissionRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("权限"));
        permission.updateInfo(cmd.getName(), cmd.getPath(), cmd.getSortOrder(),
                cmd.getVisible(), cmd.getStatus(), cmd.getRemark());
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
