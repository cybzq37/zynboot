package com.zyn.sys.controller;

import com.zyn.api.sys.response.org.OrgRes;
import com.zyn.api.sys.response.org.OrgTreeRes;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.entity.SysOrganization;
import com.zyn.sys.manager.OrgManager;
import com.zyn.sys.service.SysOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/org")
public class SysOrganizationController {

    private final SysOrganizationService organizationService;
    private final OrgManager orgManager;

    @GetMapping
    public ApiResponse<List<OrgRes>> list() {
        List<SysOrganization> orgs = organizationService.list();
        return ApiResponse.ok(BeanUtils.copyList(orgs, OrgRes.class));
    }

    @GetMapping("/tree")
    public ApiResponse<List<OrgTreeRes>> tree() {
        return ApiResponse.ok(orgManager.getOrgTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrgRes> getById(@PathVariable String id) {
        SysOrganization org = organizationService.getById(id);
        return ApiResponse.ok(BeanUtils.copy(org, OrgRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody OrgRes orgDTO) {
        SysOrganization org = BeanUtils.copy(orgDTO, SysOrganization.class);
        organizationService.save(org);
        return ApiResponse.ok(null);
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody OrgRes orgDTO) {
        SysOrganization org = BeanUtils.copy(orgDTO, SysOrganization.class);
        organizationService.updateById(org);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        organizationService.removeById(id);
        return ApiResponse.ok(null);
    }
}
