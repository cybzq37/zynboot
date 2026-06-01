package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.resource.ResourceSaveCmd;
import com.zyn.sys.domain.repository.ResourceRepository;
import com.zyn.sys.infrastructure.entity.SysResource;
import com.zyn.sys.response.resource.ResourceRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resource")
public class SysResourceController {

    private final ResourceRepository resourceRepository;

    @GetMapping
    public ApiResponse<List<ResourceRes>> list() {
        return ApiResponse.ok(BeanUtils.copyList(resourceRepository.findAll(), ResourceRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceRes> getById(@PathVariable String id) {
        SysResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("资源不存在"));
        return ApiResponse.ok(BeanUtils.copy(resource, ResourceRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody ResourceSaveCmd cmd) {
        resourceRepository.save(BeanUtils.copy(cmd, SysResource.class));
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody ResourceSaveCmd cmd) {
        SysResource resource = BeanUtils.copy(cmd, SysResource.class);
        resource.setId(id);
        resourceRepository.update(resource);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        resourceRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
