package com.zyn.api.sys.response.user;

import com.zyn.api.sys.response.user.UserRes;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRes {

    String token;
    UserRes userInfo;
}
