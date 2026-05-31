package com.zyn.sys.client;

import com.zyn.sys.response.role.RoleRes;
import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@ServiceClient("sys")
@HttpExchange("/api/v1/role")
public interface RemoteRoleService {

    @GetExchange("/{id}")
    ApiResponse<RoleRes> getById(@PathVariable String id);
}
