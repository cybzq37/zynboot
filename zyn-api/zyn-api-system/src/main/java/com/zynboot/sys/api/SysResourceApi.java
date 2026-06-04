package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.resource.ResourceSaveCmd;
import com.zynboot.sys.response.resource.ResourceRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "sys", path = "/api/v1/resource")
public interface SysResourceApi {

    @GetMapping
    ApiResponse<List<ResourceRes>> list();

    @GetMapping("/{id}")
    ApiResponse<ResourceRes> getById(@PathVariable String id);

    @PostMapping
    ApiResponse<Void> create(@RequestBody ResourceSaveCmd cmd);

    @PutMapping("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody ResourceSaveCmd cmd);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
