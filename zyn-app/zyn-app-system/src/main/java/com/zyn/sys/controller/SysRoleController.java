package com.zyn.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.role.RoleSaveCmd;
import com.zyn.sys.domain.aggregate.RoleAggregate;
import com.zyn.sys.domain.repository.RoleRepository;
import com.zyn.sys.handler.query.PermissionQueryHandler;
import com.zyn.sys.handler.query.RoleQueryHandler;
import com.zyn.sys.infrastructure.entity.SysRole;
import com.zyn.sys.infrastructure.mapper.SysRoleMapper;
import com.zyn.sys.query.role.RoleQuery;
import com.zyn.sys.response.role.RoleRes;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class SysRoleController {

    private final RoleQueryHandler roleQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;
    private final RoleRepository roleRepository;
    private final SysRoleMapper roleMapper;

    @GetMapping
    public ApiResponse<List<RoleRes>> list(RoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(query.getRoleCode()), SysRole::getRoleCode, query.getRoleCode())
                .like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getSort);
        return ApiResponse.ok(BeanUtils.copyList(roleMapper.selectList(wrapper), RoleRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleRes> getById(@PathVariable String id) {
        return ApiResponse.ok(roleQueryHandler.findById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody RoleSaveCmd cmd) {
        RoleAggregate role = RoleAggregate.create(cmd.getRoleCode(), cmd.getRoleName());
        role.updateInfo(cmd.getRoleName(), cmd.getDataScope(), cmd.getRemark());
        roleRepository.save(role);
        permissionQueryHandler.clearCacheByRoleId(role.getEntity().getId());
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody RoleSaveCmd cmd) {
        RoleAggregate role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        role.updateInfo(cmd.getRoleName(), cmd.getDataScope(), cmd.getRemark());
        roleRepository.update(role);
        permissionQueryHandler.clearCacheByRoleId(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionQueryHandler.clearCacheByRoleId(id);
        roleMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
