package com.zynboot.sys.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.BeanUtils;
import com.zynboot.sys.command.resource.ResourceSaveCmd;
import com.zynboot.sys.domain.aggregate.ResourceAggregate;
import com.zynboot.sys.domain.repository.ResourceRepository;
import com.zynboot.sys.response.resource.ResourceRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.zynboot.infra.web.version.ApiVersion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/resource")
public class SysResourceController {

    private final ResourceRepository resourceRepository;

    @GetMapping
    public ApiResponse<List<ResourceRes>> list() {
        return ApiResponse.ok(BeanUtils.copyList(resourceRepository.findAll(), ResourceRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceRes> getById(@PathVariable String id) {
        ResourceAggregate resource = resourceRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("资源"));
        return ApiResponse.ok(BeanUtils.copy(resource.getEntity(), ResourceRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody ResourceSaveCmd cmd) {
        ResourceAggregate resource = ResourceAggregate.create(
                cmd.getPermissionId(), cmd.getName(), cmd.getType(),
                cmd.getRequestMethod(), cmd.getRequestPath());
        resourceRepository.save(resource);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody ResourceSaveCmd cmd) {
        ResourceAggregate resource = resourceRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("资源"));
        resource.updateInfo(cmd.getName(), cmd.getRequestMethod(), cmd.getRequestPath(),
                cmd.getStatus(), cmd.getRemark());
        resourceRepository.update(resource);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        resourceRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
