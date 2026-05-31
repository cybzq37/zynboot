package com.zyn.sys.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.api.sys.response.permission.MenuTreeRes;
import com.zyn.sys.entity.SysPermission;
import com.zyn.sys.entity.SysRole;
import com.zyn.sys.service.SysPermissionService;
import com.zyn.sys.service.SysRoleService;
import com.zyn.sys.util.CacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionManager {

    private static final String CACHE_PERM_CODES_KEY = "sys:perm:user:%s";
    private static final String CACHE_ROLE_CODES_KEY = "sys:role:user:%s";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final SysPermissionService permissionService;
    private final SysRoleService roleService;
    private final CacheHelper cacheHelper;

    public Set<String> getPermCodes(String userId) {
        return cacheHelper.getOrLoad(CACHE_PERM_CODES_KEY.formatted(userId), CACHE_TTL, () ->
                permissionService.getPermCodesByUserId(userId).stream().collect(Collectors.toSet()));
    }

    public List<SysPermission> getPermsByUserId(String userId) {
        return permissionService.getPermsByUserId(userId);
    }

    public Set<String> getRoleCodes(String userId) {
        return cacheHelper.getOrLoad(CACHE_ROLE_CODES_KEY.formatted(userId), CACHE_TTL, () ->
                roleService.getRolesByUserId(userId).stream()
                        .map(SysRole::getRoleCode).collect(Collectors.toSet()));
    }

    public List<MenuTreeRes> getPermissionTree() {
        List<SysPermission> all = permissionService.list(
                new LambdaQueryWrapper<SysPermission>()
                        .in(SysPermission::getPermType, 1, 2)
                        .eq(SysPermission::getStatus, 1)
                        .orderByAsc(SysPermission::getSort)
        );
        return buildTree(all, "0");
    }

    private List<MenuTreeRes> buildTree(List<SysPermission> all, String parentId) {
        Map<String, List<SysPermission>> parentMap = all.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getParentId() == null ? "0" : p.getParentId(),
                        Collectors.toList()
                ));
        return parentMap.getOrDefault(parentId, List.of()).stream()
                .map(p -> MenuTreeRes.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .permName(p.getPermName())
                        .permType(p.getPermType())
                        .path(p.getPath())
                        .sort(p.getSort())
                        .visible(p.getVisible())
                        .children(buildTree(all, p.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public void clearCache(String userId) {
        cacheHelper.evict(
                CACHE_PERM_CODES_KEY.formatted(userId),
                CACHE_ROLE_CODES_KEY.formatted(userId));
    }
}
