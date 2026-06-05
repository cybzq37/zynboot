package com.zynboot.sys.response.user;

import com.zynboot.sys.response.permission.MenuTreeRes;
import com.zynboot.sys.response.user.UserRes;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import lombok.Value;

import java.util.List;

@Value
@Jacksonized
@Builder
public class UserInfoRes {

    UserRes user;
    List<String> roles;
    List<String> permissions;
    List<MenuTreeRes> menus;
}
