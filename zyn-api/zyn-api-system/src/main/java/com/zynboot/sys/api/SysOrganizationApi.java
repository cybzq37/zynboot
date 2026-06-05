package com.zynboot.sys.api;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.command.org.OrgSaveCmd;
import com.zynboot.sys.response.org.OrgRes;
import com.zynboot.sys.response.org.OrgTreeRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "zyn-sys", contextId = "SysOrganizationApi", path = "/api/v1/org")
public interface SysOrganizationApi {

    @GetMapping
    ApiResponse<List<OrgRes>> list();

    @GetMapping("/tree")
    ApiResponse<List<OrgTreeRes>> tree();

    @GetMapping("/{id}")
    ApiResponse<OrgRes> getById(@PathVariable String id);

    @PostMapping
    ApiResponse<Void> create(@RequestBody OrgSaveCmd cmd);

    @PutMapping("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody OrgSaveCmd cmd);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
