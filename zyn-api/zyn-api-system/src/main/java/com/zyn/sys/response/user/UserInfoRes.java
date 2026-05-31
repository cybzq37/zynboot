package com.zyn.sys.response.user;

import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.user.UserRes;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UserInfoRes {

    UserRes user;
    List<String> roles;
    List<String> permissions;
    List<MenuTreeRes> menus;
}
