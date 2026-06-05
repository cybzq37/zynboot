package com.zynboot.sys.handler.command;

import com.zynboot.sys.response.user.LoginRes;
import com.zynboot.sys.response.user.LoginUserRes;
import com.zynboot.sys.response.user.UserRes;
import com.zynboot.sys.response.user.UserInfoRes;
import com.zynboot.sys.response.permission.MenuTreeRes;
import com.zynboot.infra.satoken.utils.LoginHelper;
import com.zynboot.kit.exception.BaseException;
import com.zynboot.sys.domain.aggregate.UserAggregate;
import com.zynboot.sys.domain.repository.UserRepository;
import com.zynboot.sys.handler.query.PermissionQueryHandler;
import com.zynboot.sys.handler.query.UserQueryHandler;
import com.zynboot.sys.infrastructure.entity.SysPermission;
import com.zynboot.sys.util.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证命令处理器（写操作）。
 */
@Service
@RequiredArgsConstructor
public class AuthCommandHandlerImpl implements AuthCommandHandler {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final UserQueryHandler userQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public LoginRes login(String username, String password) {
        UserAggregate user = userRepository.findByUsername(username)
                .orElseThrow(() -> BaseException.badRequest("用户名或密码错误"));

        if (!PasswordUtils.matches(password, user.getPassword())) {
            user.recordLoginFailure(MAX_LOGIN_ATTEMPTS);
            userRepository.update(user);
            throw BaseException.badRequest("用户名或密码错误");
        }
        if (user.isDisabled()) {
            throw BaseException.badRequest("账号已被停用");
        }
        if (user.isLocked()) {
            throw BaseException.badRequest("账号已被锁定");
        }

        String clientIp = extractClientIp();
        user.recordLoginSuccess(clientIp);
        userRepository.update(user);

        LoginUserRes loginUser = userQueryHandler.getLoginUser(user.getId());
        LoginHelper.login(user.getId(), null, loginUser);

        UserRes userRes = UserRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .status(user.getStatus())
                .build();
        String token = cn.dev33.satoken.stp.StpUtil.getTokenValue();
        return LoginRes.builder().token(token).userInfo(userRes).build();
    }

    @Override
    public UserInfoRes getCurrentUserInfo() {
        LoginUserRes loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw BaseException.badRequest("未登录");
        }

        UserRes userRes = UserRes.builder()
                .id(loginUser.getUserId())
                .username(loginUser.getUsername())
                .nickname(loginUser.getNickname())
                .realName(loginUser.getRealName())
                .email(loginUser.getEmail())
                .phone(loginUser.getPhone())
                .avatar(loginUser.getAvatar())
                .status(loginUser.getStatus())
                .build();

        List<SysPermission> menus = permissionQueryHandler.getPermsByUserId(loginUser.getUserId());
        List<MenuTreeRes> menuTree = menus.stream()
                .filter(p -> p.getType() <= 2)
                .map(p -> MenuTreeRes.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .name(p.getName())
                        .type(p.getType())
                        .path(p.getPath())
                        .sortOrder(p.getSortOrder())
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

    private String extractClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
