package com.zyn.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.permission.PermissionSaveCmd;
import com.zyn.sys.handler.query.PermissionQueryHandler;
import com.zyn.sys.infrastructure.entity.SysPermission;
import com.zyn.sys.infrastructure.mapper.SysPermissionMapper;
import com.zyn.sys.query.permission.PermissionQuery;
import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.permission.PermissionRes;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permission")
public class SysPermissionController {

    private final PermissionQueryHandler permissionQueryHandler;
    private final SysPermissionMapper permissionMapper;

    @GetMapping
    public ApiResponse<List<PermissionRes>> list(PermissionQuery query) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<SysPermission>()
                .like(StringUtils.hasText(query.getPermName()), SysPermission::getPermName, query.getPermName())
                .eq(query.getPermType() != null, SysPermission::getPermType, query.getPermType())
                .eq(query.getStatus() != null, SysPermission::getStatus, query.getStatus())
                .orderByAsc(SysPermission::getSort);
        return ApiResponse.ok(BeanUtils.copyList(permissionMapper.selectList(wrapper), PermissionRes.class));
    }

    @GetMapping("/tree")
    public ApiResponse<List<MenuTreeRes>> tree() {
        return ApiResponse.ok(permissionQueryHandler.getPermissionTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionRes> getById(@PathVariable String id) {
        SysPermission permission = permissionMapper.selectById(id);
        return ApiResponse.ok(BeanUtils.copy(permission, PermissionRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody PermissionSaveCmd cmd) {
        SysPermission permission = BeanUtils.copy(cmd, SysPermission.class);
        permissionMapper.insert(permission);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody PermissionSaveCmd cmd) {
        SysPermission permission = BeanUtils.copy(cmd, SysPermission.class);
        permission.setId(id);
        permissionMapper.updateById(permission);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
