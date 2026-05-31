package com.zyn.sys.api;
import com.zyn.infra.exchange.ExchangeClient;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.permission.PermissionSaveCmd;
import com.zyn.sys.query.permission.PermissionQuery;
import com.zyn.sys.response.permission.MenuTreeRes;
import com.zyn.sys.response.permission.PermissionRes;
import com.zyn.infra.exchange.query.HttpQuery;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

@ExchangeClient("sys")
@HttpExchange("/sys/api/v1/permission")
public interface SysPermissionApi {

    @GetExchange
    ApiResponse<List<PermissionRes>> list(@HttpQuery PermissionQuery query);

    @GetExchange("/tree")
    ApiResponse<List<MenuTreeRes>> tree();

    @GetExchange("/{id}")
    ApiResponse<PermissionRes> getById(@PathVariable String id);

    @PostExchange
    ApiResponse<Void> create(@RequestBody PermissionSaveCmd cmd);

    @PutExchange("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody PermissionSaveCmd cmd);

    @DeleteExchange("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
