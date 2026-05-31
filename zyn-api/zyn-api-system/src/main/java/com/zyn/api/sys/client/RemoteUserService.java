package com.zyn.api.sys.client;

import com.zyn.api.sys.response.user.UserRes;
import com.zyn.api.sys.response.user.UserInfoRes;
import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@ServiceClient("sys")
@HttpExchange("/api/v1/user")
public interface RemoteUserService {

    @GetExchange("/{id}")
    ApiResponse<UserRes> getById(@PathVariable String id);

    @GetExchange("/info")
    ApiResponse<UserInfoRes> getUserInfo();
}
