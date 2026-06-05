package com.zynboot.sys.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.BeanUtils;
import com.zynboot.sys.command.org.OrgSaveCmd;
import com.zynboot.sys.domain.aggregate.OrgAggregate;
import com.zynboot.sys.domain.repository.OrgRepository;
import com.zynboot.sys.handler.query.OrgQueryHandler;
import com.zynboot.sys.response.org.OrgRes;
import com.zynboot.sys.response.org.OrgTreeRes;
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
                .orElseThrow(() -> BizException.notFound("组织"));
        return ApiResponse.ok(BeanUtils.copy(org, OrgRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = OrgAggregate.create(cmd.getCode(), cmd.getName(), cmd.getType());
        org.setParentId(cmd.getParentId());
        org.updateInfo(cmd.getName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.save(org);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody OrgSaveCmd cmd) {
        OrgAggregate org = orgRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("组织"));
        org.updateInfo(cmd.getName(), cmd.getPhone(), cmd.getEmail(), cmd.getRemark());
        orgRepository.update(org);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        orgRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
