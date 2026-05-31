package com.zyn.api.sys.query.user;

import lombok.Data;

@Data
public class UserQuery {

    private String username;
    private String nickname;
    private String phone;
    private Integer status;
}
