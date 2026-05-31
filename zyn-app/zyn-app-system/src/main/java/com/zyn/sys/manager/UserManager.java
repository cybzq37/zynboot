package com.zyn.sys.manager;

import com.zyn.api.sys.response.user.LoginUserRes;
import com.zyn.sys.entity.SysRole;
import com.zyn.sys.entity.SysUser;
import com.zyn.sys.service.SysPermissionService;
import com.zyn.sys.service.SysRoleService;
import com.zyn.sys.service.SysUserService;
import com.zyn.sys.util.CacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserManager {

    private static final String CACHE_USER_KEY = "sys:user:login:%s";
    private static final String CACHE_PERM_KEY = "sys:perm:user:%s";
    private static final String CACHE_ROLE_KEY = "sys:role:user:%s";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final SysUserService userService;
    private final SysRoleService roleService;
    private final SysPermissionService permissionService;
    private final CacheHelper cacheHelper;

    public LoginUserRes getLoginUser(String userId) {
        return cacheHelper.getOrLoad(CACHE_USER_KEY.formatted(userId), CACHE_TTL, () -> {
            SysUser user = userService.getById(userId);
            if (user == null) {
                return null;
            }
            Set<String> roleCodes = roleService.getRolesByUserId(userId).stream()
                    .map(SysRole::getRoleCode).collect(Collectors.toSet());
            Set<String> permCodes = permissionService.getPermCodesByUserId(userId).stream()
                    .collect(Collectors.toSet());
            return LoginUserRes.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .realName(user.getRealName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .avatar(user.getAvatar())
                    .status(user.getStatus())
                    .roleCodes(roleCodes)
                    .permCodes(permCodes)
                    .build();
        });
    }

    public Set<String> getPermCodes(String userId) {
        return cacheHelper.getOrLoad(CACHE_PERM_KEY.formatted(userId), CACHE_TTL, () ->
                permissionService.getPermCodesByUserId(userId).stream().collect(Collectors.toSet()));
    }

    public Set<String> getRoleCodes(String userId) {
        return cacheHelper.getOrLoad(CACHE_ROLE_KEY.formatted(userId), CACHE_TTL, () ->
                roleService.getRolesByUserId(userId).stream()
                        .map(SysRole::getRoleCode).collect(Collectors.toSet()));
    }

    public void clearCache(String userId) {
        cacheHelper.evict(
                CACHE_USER_KEY.formatted(userId),
                CACHE_PERM_KEY.formatted(userId),
                CACHE_ROLE_KEY.formatted(userId));
    }
}
