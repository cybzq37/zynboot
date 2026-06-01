package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.org.OrgSaveCmd;
import com.zyn.sys.domain.aggregate.OrgAggregate;
import com.zyn.sys.domain.repository.OrgRepository;
import com.zyn.sys.handler.query.OrgQueryHandler;
import com.zyn.sys.response.org.OrgRes;
import com.zyn.sys.response.org.OrgTreeRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/org")
public class SysOrganizationController {

    private final OrgQueryHandler orgQueryHandler;
    private final OrgRepository orgRepository;

    @GetMapping
    public ApiResponse<List<OrgRes>> list() {
        return ApiResponse.ok(
                orgRepository.findAll().stream()
                        .map(o -> BeanUtils.copy(o, OrgRes.class))
                        .toList()
        );
    }

    @GetMapping("/tree")
    public ApiResponse<List<OrgTreeRes>> tree() {
        return ApiResponse.ok(orgQueryHandler.getOrgTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrgRes> getById(@PathVariable String id) {
        OrgAggregate org = orgRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("组织不存在"));
        return ApiResponse.ok(BeanUtils.copy(org, OrgRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = OrgAggregate.create(cmd.getOrgCode(), cmd.getOrgName(), cmd.getOrgType());
        org.setParentId(cmd.getParentId());
        org.updateInfo(cmd.getOrgName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.save(org);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = orgRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("组织不存在"));
        org.updateInfo(cmd.getOrgName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.update(org);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        orgRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
