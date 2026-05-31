package com.zyn.sys.controller;

import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.resource.ResourceSaveCmd;
import com.zyn.sys.infrastructure.entity.SysResource;
import com.zyn.sys.infrastructure.mapper.SysResourceMapper;
import com.zyn.sys.response.resource.ResourceRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resource")
public class SysResourceController {

    private final SysResourceMapper resourceMapper;

    @GetMapping
    public ApiResponse<List<ResourceRes>> list() {
        return ApiResponse.ok(BeanUtils.copyList(resourceMapper.selectList(null), ResourceRes.class));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceRes> getById(@PathVariable String id) {
        return ApiResponse.ok(BeanUtils.copy(resourceMapper.selectById(id), ResourceRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody ResourceSaveCmd cmd) {
        resourceMapper.insert(BeanUtils.copy(cmd, SysResource.class));
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody ResourceSaveCmd cmd) {
        SysResource resource = BeanUtils.copy(cmd, SysResource.class);
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
