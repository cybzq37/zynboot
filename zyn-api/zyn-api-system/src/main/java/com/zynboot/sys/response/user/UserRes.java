package com.zynboot.sys.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
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
