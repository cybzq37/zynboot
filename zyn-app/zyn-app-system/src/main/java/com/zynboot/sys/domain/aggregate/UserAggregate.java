package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.domain.enums.UserStatus;
import com.zynboot.sys.infrastructure.entity.SysUser;

import java.time.LocalDateTime;

/**
 * 用户聚合根，封装用户相关的业务规则。
 */
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

    /** 供 Repository 层持久化使用。 */
    public SysUser getEntity() {
        return entity;
    }

    public String getId() {
        return entity.getId();
    }

    public String getUsername() {
        return entity.getUsername();
    }

    public String getPassword() {
        return entity.getPassword();
    }

    public String getNickname() {
        return entity.getNickname();
    }

    public String getRealName() {
        return entity.getRealName();
    }

    public String getEmail() {
        return entity.getEmail();
    }

    public String getPhone() {
        return entity.getPhone();
    }

    public String getAvatar() {
        return entity.getAvatar();
    }

    public Integer getGender() {
        return entity.getGender();
    }

    public Integer getStatus() {
        return entity.getStatus();
    }

    public void updateProfile(String nickname, String realName, String email,
                              String phone, String avatar, Integer gender, String remark) {
        if (nickname != null) entity.setNickname(nickname);
        if (realName != null) entity.setRealName(realName);
        if (email != null) entity.setEmail(email);
        if (phone != null) entity.setPhone(phone);
        if (avatar != null) entity.setAvatar(avatar);
        if (gender != null) entity.setGender(gender);
        if (remark != null) entity.setRemark(remark);
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
