package com.zyn.sys.service.impl;

import com.zyn.api.sys.response.user.LoginUserRes;
import com.zyn.api.sys.response.user.UserRes;
import com.zyn.api.sys.response.user.LoginRes;
import com.zyn.api.sys.response.user.UserInfoRes;
import com.zyn.api.sys.response.permission.MenuTreeRes;
import com.zyn.infra.satoken.utils.LoginHelper;
import com.zyn.kit.exception.BaseException;
import com.zyn.kit.util.BeanUtils;
import com.zyn.kit.util.IdUtils;
import com.zyn.sys.entity.SysPermission;
import com.zyn.sys.entity.SysUser;
import com.zyn.sys.manager.PermissionManager;
import com.zyn.sys.manager.UserManager;
import com.zyn.sys.service.AuthService;
import com.zyn.sys.service.SysPermissionService;
import com.zyn.sys.service.SysUserService;
import com.zyn.sys.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService userService;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final SysPermissionService permissionService;

    @Override
    public LoginRes login(String username, String password) {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw BaseException.badRequest("用户名或密码错误");
        }
        if (!PasswordUtils.matches(password, user.getPassword())) {
            throw BaseException.badRequest("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw BaseException.badRequest("账号已被停用或锁定");
        }

        LoginUserRes loginUserRes = userManager.getLoginUser(user.getId());
        LoginHelper.login(user.getId(), null, loginUserRes);

        UserRes userDTO = BeanUtils.copy(user, UserRes.class);
        String token = cn.dev33.satoken.stp.StpUtil.getTokenValue();
        return LoginRes.builder().token(token).userInfo(userDTO).build();
    }

    @Override
    public UserInfoRes getCurrentUserInfo() {
        LoginUserRes loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw BaseException.badRequest("未登录");
        }

        SysUser user = userService.getById(loginUser.getUserId());
        UserRes userDTO = BeanUtils.copy(user, UserRes.class);

        List<SysPermission> menus = permissionService.getPermsByUserId(loginUser.getUserId());
        List<MenuTreeRes> menuTree = menus.stream()
                .filter(p -> p.getPermType() <= 2)
                .map(p -> MenuTreeRes.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .permName(p.getPermName())
                        .permType(p.getPermType())
                        .path(p.getPath())
                        .sort(p.getSort())
                        .visible(p.getVisible())
                        .build())
                .collect(Collectors.toList());

        return UserInfoRes.builder()
                .user(userDTO)
                .roles(loginUser.getRoleCodes().stream().sorted().collect(Collectors.toList()))
                .permissions(loginUser.getPermCodes().stream().sorted().collect(Collectors.toList()))
                .menus(menuTree)
                .build();
    }
}
