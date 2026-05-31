package com.zyn.sys.api;
import com.zyn.infra.exchange.ExchangeClient;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.resource.ResourceSaveCmd;
import com.zyn.sys.response.resource.ResourceRes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

@ExchangeClient("sys")
@HttpExchange("/sys/api/v1/resource")
public interface SysResourceApi {

    @GetExchange
    ApiResponse<List<ResourceRes>> list();

    @GetExchange("/{id}")
    ApiResponse<ResourceRes> getById(@PathVariable String id);

    @PostExchange
    ApiResponse<Void> create(@RequestBody ResourceSaveCmd cmd);

    @PutExchange("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody ResourceSaveCmd cmd);

    @DeleteExchange("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
