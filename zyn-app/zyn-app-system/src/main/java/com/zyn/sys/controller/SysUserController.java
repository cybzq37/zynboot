package com.zyn.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyn.kit.response.ApiResponse;
import com.zyn.kit.util.BeanUtils;
import com.zyn.sys.command.user.UserSaveCmd;
import com.zyn.sys.domain.aggregate.UserAggregate;
import com.zyn.sys.domain.repository.UserRepository;
import com.zyn.sys.handler.query.PermissionQueryHandler;
import com.zyn.sys.handler.query.UserQueryHandler;
import com.zyn.sys.query.user.UserPageQuery;
import com.zyn.sys.response.user.UserRes;
import com.zyn.sys.util.PasswordUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class SysUserController {

    private final UserQueryHandler userQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> page(UserPageQuery query) {
        Page<UserAggregate> result = userRepository.page(query);
        return ApiResponse.ok(Map.of(
                "records", BeanUtils.copyList(result.getRecords(), UserRes.class),
                "total", result.getTotal(),
                "pageNum", result.getCurrent(),
                "pageSize", result.getSize()
        ));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserRes> getById(@PathVariable String id) {
        return ApiResponse.ok(userQueryHandler.findById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody UserSaveCmd cmd) {
        UserAggregate user = UserAggregate.create(cmd.getUsername(), PasswordUtils.encode(cmd.getPassword()));
        user.updateProfile(cmd.getNickname(), cmd.getRealName(), cmd.getEmail(),
                cmd.getPhone(), cmd.getAvatar(), cmd.getGender(), cmd.getRemark());
        userRepository.save(user);
        userQueryHandler.clearCache(user.getId());
        permissionQueryHandler.clearCache(user.getId());
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody UserSaveCmd cmd) {
        UserAggregate user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.updateProfile(cmd.getNickname(), cmd.getRealName(), cmd.getEmail(),
                cmd.getPhone(), cmd.getAvatar(), cmd.getGender(), cmd.getRemark());
        if (StringUtils.hasText(cmd.getPassword())) {
            user.updatePassword(PasswordUtils.encode(cmd.getPassword()));
        }
        userRepository.update(user);
        userQueryHandler.clearCache(id);
        permissionQueryHandler.clearCache(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userRepository.delete(id);
        userQueryHandler.clearCache(id);
        permissionQueryHandler.clearCache(id);
        return ApiResponse.ok(null);
    }
}
