package com.zyn.sys.client;

import com.zyn.sys.response.resource.ResourceRes;
import com.zyn.infra.discovery.ServiceClient;
import com.zyn.kit.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@ServiceClient("sys")
@HttpExchange("/api/v1/resource")
public interface RemoteResourceService {

    @GetExchange("/{id}")
    ApiResponse<ResourceRes> getById(@PathVariable String id);
}
