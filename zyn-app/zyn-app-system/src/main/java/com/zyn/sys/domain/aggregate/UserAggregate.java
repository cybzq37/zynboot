package com.zyn.sys.domain.aggregate;

import com.zyn.api.sys.command.user.UserSaveCmd;
import com.zyn.sys.domain.enums.UserStatus;
import com.zyn.sys.infrastructure.entity.SysUser;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 用户聚合根，封装用户相关的业务规则。
 */
@Getter
public class UserAggregate {

    private final SysUser entity;

    private UserAggregate(SysUser entity) {
        this.entity = entity;
    }

    public static UserAggregate from(SysUser entity) {
        return new UserAggregate(entity);
    }

    public static UserAggregate create(String username, String password) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(UserStatus.NORMAL.getCode());
        user.setLoginAttempts(0);
        return new UserAggregate(user);
    }

    public void updateProfile(UserSaveCmd cmd) {
        if (cmd.getNickname() != null) entity.setNickname(cmd.getNickname());
        if (cmd.getRealName() != null) entity.setRealName(cmd.getRealName());
        if (cmd.getEmail() != null) entity.setEmail(cmd.getEmail());
        if (cmd.getPhone() != null) entity.setPhone(cmd.getPhone());
        if (cmd.getAvatar() != null) entity.setAvatar(cmd.getAvatar());
        if (cmd.getGender() != null) entity.setGender(cmd.getGender());
        if (cmd.getRemark() != null) entity.setRemark(cmd.getRemark());
    }

    public void updatePassword(String encodedPassword) {
        entity.setPassword(encodedPassword);
        entity.setPwdUpdateTime(LocalDateTime.now());
    }

    public void recordLoginSuccess(String ip) {
        entity.setLoginIp(ip);
        entity.setLoginTime(LocalDateTime.now());
        entity.setLoginAttempts(0);
        entity.setLockTime(null);
    }

    public void recordLoginFailure(int maxAttempts) {
        int attempts = entity.getLoginAttempts() == null ? 0 : entity.getLoginAttempts();
        entity.setLoginAttempts(attempts + 1);
        if (attempts + 1 >= maxAttempts) {
            entity.setLockTime(LocalDateTime.now());
        }
    }

    public void disable() {
        entity.setStatus(UserStatus.DISABLED.getCode());
    }

    public void enable() {
        entity.setStatus(UserStatus.NORMAL.getCode());
        entity.setLoginAttempts(0);
        entity.setLockTime(null);
    }

    public boolean isLocked() {
        return entity.getLockTime() != null;
    }

    public boolean isDisabled() {
        return Integer.valueOf(UserStatus.DISABLED.getCode()).equals(entity.getStatus());
    }
}
