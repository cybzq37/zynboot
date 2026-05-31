package com.zyn.sys.handler.command;

import com.zyn.sys.response.user.LoginRes;
import com.zyn.sys.response.user.LoginUserRes;
import com.zyn.sys.response.user.UserRes;
import com.zyn.sys.response.user.UserInfoRes;
import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.infra.satoken.utils.LoginHelper;
import com.zyn.kit.exception.BaseException;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.domain.aggregate.UserAggregate;
import com.zyn.sys.domain.repository.UserRepository;
import com.zyn.sys.handler.query.PermissionQueryHandler;
import com.zyn.sys.handler.query.UserQueryHandler;
import com.zyn.sys.infrastructure.entity.SysPermission;
import com.zyn.sys.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证命令处理器（写操作）。
 */
@Service
@RequiredArgsConstructor
public class AuthCommandHandlerImpl implements AuthCommandHandler {

    private final UserRepository userRepository;
    private final UserQueryHandler userQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;

    @Override
    public LoginRes login(String username, String password) {
        UserAggregate user = userRepository.findByUsername(username)
                .orElseThrow(() -> BaseException.badRequest("用户名或密码错误"));

        if (!PasswordUtils.matches(password, user.getEntity().getPassword())) {
            throw BaseException.badRequest("用户名或密码错误");
        }
        if (user.isDisabled()) {
            throw BaseException.badRequest("账号已被停用");
        }
        if (user.isLocked()) {
            throw BaseException.badRequest("账号已被锁定");
        }

        user.recordLoginSuccess(null);
        userRepository.update(user);

        LoginUserRes loginUser = userQueryHandler.getLoginUser(user.getEntity().getId());
        LoginHelper.login(user.getEntity().getId(), null, loginUser);

        UserRes userRes = BeanUtils.copy(user.getEntity(), UserRes.class);
        String token = cn.dev33.satoken.stp.StpUtil.getTokenValue();
        return LoginRes.builder().token(token).userInfo(userRes).build();
    }

    @Override
    public UserInfoRes getCurrentUserInfo() {
        LoginUserRes loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw BaseException.badRequest("未登录");
        }

        UserRes userRes = BeanUtils.copy(
                userRepository.findById(loginUser.getUserId())
                        .map(UserAggregate::getEntity)
                        .orElse(null),
                UserRes.class);

        List<SysPermission> menus = permissionQueryHandler.getPermsByUserId(loginUser.getUserId());
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
                .user(userRes)
                .roles(loginUser.getRoleCodes().stream().sorted().collect(Collectors.toList()))
                .permissions(loginUser.getPermCodes().stream().sorted().collect(Collectors.toList()))
                .menus(menuTree)
                .build();
    }
}
