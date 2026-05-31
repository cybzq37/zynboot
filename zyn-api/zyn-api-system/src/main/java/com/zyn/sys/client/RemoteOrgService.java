package com.zyn.sys.client;

import com.zyn.sys.response.org.OrgRes;
import com.zyn.sys.response.org.OrgTreeRes;
import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@ServiceClient("sys")
@HttpExchange("/api/v1/org")
public interface RemoteOrgService {

    @GetExchange("/{id}")
    ApiResponse<OrgRes> getById(@PathVariable String id);

    @GetExchange("/tree")
    ApiResponse<List<OrgTreeRes>> tree();
}
