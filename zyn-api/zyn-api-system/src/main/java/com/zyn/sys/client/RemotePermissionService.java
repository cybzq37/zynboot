package com.zyn.sys.client;

import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.permission.PermissionRes;
import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@ServiceClient("sys")
@HttpExchange("/api/v1/permission")
public interface RemotePermissionService {

    @GetExchange("/{id}")
    ApiResponse<PermissionRes> getById(@PathVariable String id);

    @GetExchange("/tree")
    ApiResponse<List<MenuTreeRes>> tree();
}
