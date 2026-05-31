package com.zyn.sys.controller;

import com.zyn.sys.command.org.OrgSaveCmd;
import com.zyn.sys.response.org.OrgRes;
import com.zyn.sys.response.org.OrgTreeRes;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.api.SysOrganizationApi;
import com.zyn.sys.domain.aggregate.OrgAggregate;
import com.zyn.sys.domain.repository.OrgRepository;
import com.zyn.sys.handler.query.OrgQueryHandler;
import com.zyn.sys.infrastructure.entity.SysOrganization;
import com.zyn.sys.infrastructure.mapper.SysOrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织管理控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/org")
public class SysOrganizationController implements SysOrganizationApi {

    private final OrgQueryHandler orgQueryHandler;
    private final OrgRepository orgRepository;
    private final SysOrganizationMapper orgMapper;

    @Override
    @GetMapping
    public ApiResponse<List<OrgRes>> list() {
        return ApiResponse.ok(
                orgMapper.selectList(null).stream()
                        .map(o -> BeanUtils.copy(o, OrgRes.class))
                        .toList()
        );
    }

    @Override
    @GetMapping("/tree")
    public ApiResponse<List<OrgTreeRes>> tree() {
        return ApiResponse.ok(orgQueryHandler.getOrgTree());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<OrgRes> getById(@PathVariable String id) {
        SysOrganization org = orgMapper.selectById(id);
        return ApiResponse.ok(BeanUtils.copy(org, OrgRes.class));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> create(@RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = OrgAggregate.create(cmd.getOrgCode(), cmd.getOrgName(), cmd.getOrgType());
        org.updateInfo(cmd.getOrgName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.save(org);
        return ApiResponse.ok(null);
    }

    @Override
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = orgRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("组织不存在"));
        org.updateInfo(cmd.getOrgName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.update(org);
        return ApiResponse.ok(null);
    }

    @Override
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        orgMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
