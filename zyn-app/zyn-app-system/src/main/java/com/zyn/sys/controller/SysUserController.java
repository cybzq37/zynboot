package com.zyn.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyn.sys.command.user.UserSaveCmd;
import com.zyn.sys.query.user.UserPageQuery;
import com.zyn.sys.response.user.UserRes;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.api.SysUserApi;
import com.zyn.sys.domain.aggregate.UserAggregate;
import com.zyn.sys.domain.repository.UserRepository;
import com.zyn.sys.handler.query.UserQueryHandler;
import com.zyn.sys.infrastructure.entity.SysUser;
import com.zyn.sys.infrastructure.mapper.SysUserMapper;
import com.zyn.sys.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器（REST 入口，薄层）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class SysUserController implements SysUserApi {

    private final UserQueryHandler userQueryHandler;
    private final UserRepository userRepository;
    private final SysUserMapper userMapper;

    @Override
    @GetMapping
    public ApiResponse<Map<String, Object>> page(UserPageQuery query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
                .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
                .like(StringUtils.hasText(query.getPhone()), SysUser::getPhone, query.getPhone())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        return ApiResponse.ok(Map.of(
                "records", BeanUtils.copyList(result.getRecords(), UserRes.class),
                "total", result.getTotal(),
                "pageNum", result.getCurrent(),
                "pageSize", result.getSize()
        ));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<UserRes> getById(@PathVariable String id) {
        return ApiResponse.ok(userQueryHandler.findById(id));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> create(@RequestBody UserSaveCmd cmd) {
        UserAggregate user = UserAggregate.create(cmd.getUsername(), PasswordUtils.encode(cmd.getPassword()));
        user.updateProfile(cmd);
        userRepository.save(user);
        return ApiResponse.ok(null);
    }

    @Override
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @RequestBody UserSaveCmd cmd) {
        UserAggregate user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.updateProfile(cmd);
        if (StringUtils.hasText(cmd.getPassword())) {
            user.updatePassword(PasswordUtils.encode(cmd.getPassword()));
        }
        userRepository.update(user);
        return ApiResponse.ok(null);
    }

    @Override
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userMapper.deleteById(id);
        return ApiResponse.ok(null);
    }
}
