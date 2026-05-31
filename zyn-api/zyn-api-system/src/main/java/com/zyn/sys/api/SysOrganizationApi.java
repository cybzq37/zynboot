package com.zyn.sys.api;
import com.zyn.infra.discovery.ServiceClient;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.org.OrgSaveCmd;
import com.zyn.sys.response.org.OrgRes;
import com.zyn.sys.response.org.OrgTreeRes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

@ServiceClient("sys")
@HttpExchange("/sys/api/v1/org")
public interface SysOrganizationApi {

    @GetExchange
    ApiResponse<List<OrgRes>> list();

    @GetExchange("/tree")
    ApiResponse<List<OrgTreeRes>> tree();

    @GetExchange("/{id}")
    ApiResponse<OrgRes> getById(@PathVariable String id);

    @PostExchange
    ApiResponse<Void> create(@RequestBody OrgSaveCmd cmd);

    @PutExchange("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody OrgSaveCmd cmd);

    @DeleteExchange("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
