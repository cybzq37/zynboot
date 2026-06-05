package com.zynboot.sys.handler.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.sys.response.permission.MenuTreeRes;
import com.zynboot.sys.infrastructure.entity.SysPermission;
import com.zynboot.sys.infrastructure.entity.SysRole;
import com.zynboot.sys.infrastructure.entity.SysRolePermission;
import com.zynboot.sys.infrastructure.entity.SysUserRole;
import com.zynboot.sys.infrastructure.mapper.SysPermissionMapper;
import com.zynboot.sys.infrastructure.mapper.SysRoleMapper;
import com.zynboot.sys.infrastructure.mapper.SysRolePermissionMapper;
import com.zynboot.sys.infrastructure.mapper.SysUserRoleMapper;
import com.zynboot.sys.util.CacheHelper;
import com.zynboot.sys.util.CacheKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限查询处理器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionQueryHandler {

    private final SysPermissionMapper permissionMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final CacheHelper cacheHelper;

    public Set<String> getPermCodes(String userId) {
        return cacheHelper.getOrLoad(CacheKeys.USER_PERM_CODES.formatted(userId), CacheKeys.DEFAULT_TTL, () ->
                new HashSet<>(permissionMapper.selectPermCodesByUserId(userId)));
    }

    public List<SysPermission> getPermsByUserId(String userId) {
        return permissionMapper.selectPermsByUserId(userId);
    }

    public Set<String> getRoleCodes(String userId) {
        return cacheHelper.getOrLoad(CacheKeys.USER_ROLE_CODES.formatted(userId), CacheKeys.DEFAULT_TTL, () ->
                roleMapper.selectRolesByUserId(userId).stream()
                        .map(SysRole::getCode)
                        .collect(Collectors.toSet()));
    }

    public List<MenuTreeRes> getPermissionTree() {
        return cacheHelper.getOrLoad(CacheKeys.PERM_TREE, CacheKeys.TREE_TTL, () -> {
            List<SysPermission> all = permissionMapper.selectList(
                    new LambdaQueryWrapper<SysPermission>()
                            .in(SysPermission::getType, 1, 2)
                            .eq(SysPermission::getStatus, 1)
                            .orderByAsc(SysPermission::getSortOrder)
            );
            return buildTree(all, null);
        });
    }

    private List<MenuTreeRes> buildTree(List<SysPermission> all, String parentId) {
        Map<String, List<SysPermission>> parentMap = all.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getParentId() == null ? "__root__" : p.getParentId(),
                        Collectors.toList()
                ));
        String key = parentId == null ? "__root__" : parentId;
        return parentMap.getOrDefault(key, List.of()).stream()
                .map(p -> MenuTreeRes.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .name(p.getName())
                        .type(p.getType())
                        .path(p.getPath())
                        .sortOrder(p.getSortOrder())
                        .visible(p.getVisible())
                        .children(buildTree(all, p.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public void clearCache(String userId) {
        cacheHelper.evict(
                CacheKeys.USER_PERM_CODES.formatted(userId),
                CacheKeys.USER_ROLE_CODES.formatted(userId));
    }

    public void clearCacheByRoleId(String roleId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
        userRoles.forEach(ur -> clearCache(ur.getUserId()));
    }

    public void clearCacheByPermissionId(String permissionId) {
        cacheHelper.evict(CacheKeys.PERM_TREE);
        List<SysRolePermission> rolePerms = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getPermissionId, permissionId));
        rolePerms.forEach(rp -> clearCacheByRoleId(rp.getRoleId()));
    }
}
