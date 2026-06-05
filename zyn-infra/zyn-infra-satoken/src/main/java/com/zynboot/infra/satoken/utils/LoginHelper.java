package com.zynboot.infra.satoken.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.zynboot.infra.satoken.config.SaTokenProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录鉴权助手（静态工具类）。
 * <p>
 * 通过 {@link com.zynboot.infra.satoken.config.SaTokenConfig} 在启动时注入配置。
 */
@Slf4j
public final class LoginHelper {

    private static final String LOGIN_USER_KEY = "loginUser";
    private static final String USER_ID_KEY = "userId";

    private static volatile SaTokenProperties properties;

    private LoginHelper() {}

    public static void init(SaTokenProperties props) {
        if (props == null) {
            throw new IllegalArgumentException("SaTokenProperties must not be null");
        }
        LoginHelper.properties = props;
    }

    public static void login(Object loginId, Long userId, Object loginUser) {
        loginByDevice(loginId, userId, loginUser, null);
    }

    public static void loginByDevice(Object loginId, Long userId, Object loginUser, String device) {
        if (loginId == null) {
            throw new IllegalArgumentException("loginId must not be null");
        }

        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUser);
        storage.set(USER_ID_KEY, userId);

        SaLoginModel model = new SaLoginModel();
        if (device != null && !device.isBlank()) {
            model.setDevice(device);
        }

        StpUtil.login(loginId, model.setExtra(USER_ID_KEY, userId));
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getLoginUser() {
        T loginUser = (T) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUser != null) {
            return loginUser;
        }

        SaSession session = StpUtil.getTokenSession();
        if (session == null) {
            return null;
        }

        loginUser = (T) session.get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        return session == null ? null : (T) session.get(LOGIN_USER_KEY);
    }

    public static Long getUserId() {
        Object userId = SaHolder.getStorage().get(USER_ID_KEY);
        if (userId == null) {
            try {
                userId = StpUtil.getExtra(USER_ID_KEY);
                SaHolder.getStorage().set(USER_ID_KEY, userId);
            } catch (Exception e) {
                log.warn("Failed to get userId from token extra", e);
                return null;
            }
        }
        return parseUserId(userId);
    }

    public static boolean isRoot(Long userId) {
        SaTokenProperties props = properties;
        return userId != null && props != null && userId.equals(props.getRootUserId());
    }

    public static boolean isRoot() {
        return isRoot(getUserId());
    }

    private static Long parseUserId(Object userIdObj) {
        if (userIdObj == null) {
            return null;
        }
        if (userIdObj instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(userIdObj));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
