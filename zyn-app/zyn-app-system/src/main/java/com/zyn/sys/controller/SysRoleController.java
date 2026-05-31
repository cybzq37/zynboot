package com.zyn.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.api.sys.response.role.RoleRes;
import com.zyn.api.sys.query.role.RoleQuery;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.entity.SysRole;
import com.zyn.sys.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleRes>> list(RoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(query.getRoleCode()), SysRole::getRoleCode, query.getRoleCode())
                .like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getSort);
        List<SysRole> roles = roleService.list(wrapper);
        return ApiResponse.ok(BeanUtils.copyList(roles, RoleRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleRes> getById(@PathVariable String id) {
        SysRole role = roleService.getById(id);
        return ApiResponse.ok(BeanUtils.copy(role, RoleRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody RoleRes roleDTO) {
        SysRole role = BeanUtils.copy(roleDTO, SysRole.class);
        roleService.save(role);
        return ApiResponse.ok(null);
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody RoleRes roleDTO) {
        SysRole role = BeanUtils.copy(roleDTO, SysRole.class);
        roleService.updateById(role);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        roleService.removeById(id);
        return ApiResponse.ok(null);
    }
}
