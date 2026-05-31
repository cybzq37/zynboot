package com.zyn.api.sys.command.user;

import lombok.Builder;
import lombok.Value;

/**
 * 用户保存命令（创建 / 更新）。
 * <p>
 * id 为空时创建，id 非空时更新。
 */
@Value
@Builder
public class UserSaveCmd {

    /** 用户 ID（更新时必填，创建时为空）。 */
    String id;

    /** 登录用户名。 */
    String username;

    /** 密码（创建时必填，更新时可选）。 */
    String password;

    /** 昵称。 */
    String nickname;

    /** 真实姓名。 */
    String realName;

    /** 邮箱。 */
    String email;

    /** 手机号。 */
    String phone;

    /** 头像 URL。 */
    String avatar;

    /** 性别：0=未知 1=男 2=女。 */
    Integer gender;

    /** 状态：0=禁用 1=启用。 */
    Integer status;

    /** 备注。 */
    String remark;
}
