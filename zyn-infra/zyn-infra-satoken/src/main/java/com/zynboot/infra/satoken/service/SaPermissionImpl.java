package com.zynboot.infra.satoken.service;

import cn.dev33.satoken.stp.StpInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限接口默认实现。
 * <p>
 * <b>安全提示：</b>此实现返回空列表，意味着所有权限检查均不通过。
 * 业务模块必须通过 {@code @ConditionalOnMissingBean} 覆盖此实现，
 * 否则 {@code @SaCheckPermission} 等注解将拒绝所有请求。
 */
@Slf4j
public class SaPermissionImpl implements StpInterface {

    public SaPermissionImpl() {
        log.warn("Using default empty StpInterface — all permission checks will deny access. " +
                "Override this bean in your business module to provide actual permission logic.");
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Collections.emptyList();
    }
}
