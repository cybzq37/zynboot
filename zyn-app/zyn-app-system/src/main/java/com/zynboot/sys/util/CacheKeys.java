package com.zynboot.sys.util;

import java.time.Duration;

/**
 * 缓存 Key 模板和 TTL 统一定义。
 * <p>
 * 避免各 QueryHandler 重复定义导致 Key 分歧。
 */
public final class CacheKeys {

    private CacheKeys() {}

    public static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    public static final Duration TREE_TTL = Duration.ofMinutes(10);

    /** 用户登录信息缓存：sys:user:login:{userId} */
    public static final String USER_LOGIN = "sys:user:login:%s";

    /** 用户权限码缓存：sys:perm:user:{userId} */
    public static final String USER_PERM_CODES = "sys:perm:user:%s";

    /** 用户角色码缓存：sys:role:user:{userId} */
    public static final String USER_ROLE_CODES = "sys:role:user:%s";

    /** 权限菜单树缓存 */
    public static final String PERM_TREE = "sys:perm:tree";

    /** 组织树缓存 */
    public static final String ORG_TREE = "sys:org:tree";
}
