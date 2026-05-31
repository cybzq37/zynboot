package com.zyn.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyn.api.sys.response.user.UserRes;
import com.zyn.api.sys.query.user.UserPageQuery;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.entity.SysUser;
import com.zyn.sys.service.SysUserService;
import com.zyn.sys.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class SysUserController {

    private final SysUserService userService;

    @GetMapping
    public ApiResponse<Map<String, Object>> page(UserPageQuery query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
                .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
                .like(StringUtils.hasText(query.getPhone()), SysUser::getPhone, query.getPhone())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userService.page(page, wrapper);
        return ApiResponse.ok(Map.of(
                "records", BeanUtils.copyList(result.getRecords(), UserRes.class),
                "total", result.getTotal(),
                "pageNum", result.getCurrent(),
                "pageSize", result.getSize()
        ));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserRes> getById(@PathVariable String id) {
        SysUser user = userService.getById(id);
        return ApiResponse.ok(BeanUtils.copy(user, UserRes.class));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody SysUser user) {
        user.setPassword(PasswordUtils.encode(user.getPassword()));
        userService.save(user);
        return ApiResponse.ok(null);
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody SysUser user) {
        user.setPassword(null);
        userService.updateById(user);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userService.removeById(id);
        return ApiResponse.ok(null);
    }
}
