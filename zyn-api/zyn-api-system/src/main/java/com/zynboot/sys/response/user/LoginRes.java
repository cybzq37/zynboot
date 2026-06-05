package com.zynboot.sys.response.user;

import com.zynboot.sys.response.user.UserRes;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class LoginRes {

    String token;
    UserRes userInfo;
}
