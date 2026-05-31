package com.zyn.sys.controller;

import com.zyn.sys.command.resource.ResourceSaveCmd;
import com.zyn.sys.response.resource.ResourceRes;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.api.SysResourceApi;
import com.zyn.sys.infrastructure.entity.SysResource;
import com.zyn.sys.infrastructure.mapper.SysResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 资源管理控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resource")
public class SysResourceController implements SysResourceApi {

    private final SysResourceMapper resourceMapper;

    @Override
    @GetMapping
    public ApiResponse<List<ResourceRes>> list() {
        return ApiResponse.ok(BeanUtils.copyList(resourceMapper.selectList(null), ResourceRes.class));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ResourceRes> getById(@PathVariable String id) {
        SysResource resource = resourceMapper.selectById(id);
        return ApiResponse.ok(BeanUtils.copy(resource, ResourceRes.class));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> create(@RequestBody ResourceSaveCmd cmd) {
        SysResource resource = BeanUtils.copy(cmd, SysResource.class);
        resourceMapper.insert(resource);
        return ApiResponse.ok(null);
    }

    @Override
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody ResourceSaveCmd cmd) {
        SysResource resource = BeanUtils.copy(cmd, SysResource.class);
        resource.setId(id);
        resourceMapper.updateById(resource);
        return ApiResponse.ok(null);
    }

    @Override
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        resourceMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
