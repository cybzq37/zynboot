package com.zynboot.sys.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * 用户信息响应（不可变）。
 */
@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class UserRes {

    String id;
    String username;
    String nickname;
    String realName;
    String email;
    String phone;
    String avatar;
    Integer gender;
    Integer status;
    String remark;
}
