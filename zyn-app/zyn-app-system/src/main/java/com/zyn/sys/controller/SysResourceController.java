package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.api.sys.response.permission.PermissionRes;
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
public class SysResourceController {

    private final SysResourceMapper resourceMapper;

    @GetMapping
    public ApiResponse<List<PermissionRes>> list() {
        return ApiResponse.ok(BeanUtils.copyList(resourceMapper.selectList(null), PermissionRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionRes> getById(@PathVariable String id) {
        SysResource resource = resourceMapper.selectById(id);
        return ApiResponse.ok(BeanUtils.copy(resource, PermissionRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody SysResource resource) {
        resourceMapper.insert(resource);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody SysResource resource) {
        resource.setId(id);
        resourceMapper.updateById(resource);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        resourceMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
