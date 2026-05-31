package com.zyn.sys.api;
import com.zyn.infra.discovery.ServiceClient;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.role.RoleSaveCmd;
import com.zyn.sys.query.role.RoleQuery;
import com.zyn.sys.response.role.RoleRes;
import com.zyn.infra.discovery.query.HttpQueryMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

@ServiceClient("sys")
@HttpExchange("/api/v1/role")
public interface SysRoleApi {

    @GetExchange
    ApiResponse<List<RoleRes>> list(@HttpQueryMap RoleQuery query);

    @GetExchange("/{id}")
    ApiResponse<RoleRes> getById(@PathVariable String id);

    @PostExchange
    ApiResponse<Void> create(@RequestBody RoleSaveCmd cmd);

    @PutExchange("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody RoleSaveCmd cmd);

    @DeleteExchange("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
