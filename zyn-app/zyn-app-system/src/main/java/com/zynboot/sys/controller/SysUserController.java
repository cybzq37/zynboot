package com.zynboot.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.BeanUtils;
import com.zynboot.sys.command.user.UserSaveCmd;
import com.zynboot.sys.domain.aggregate.UserAggregate;
import com.zynboot.sys.domain.repository.UserRepository;
import com.zynboot.sys.handler.query.PermissionQueryHandler;
import com.zynboot.sys.handler.query.UserQueryHandler;
import com.zynboot.sys.query.user.UserPageQuery;
import com.zynboot.sys.response.PageRes;
import com.zynboot.sys.response.user.UserRes;
import com.zynboot.sys.util.PasswordUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import com.zynboot.infra.web.version.ApiVersion;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/user")
public class SysUserController {

    private final UserQueryHandler userQueryHandler;
    private final PermissionQueryHandler permissionQueryHandler;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<PageRes<UserRes>> page(UserPageQuery query) {
        Page<UserAggregate> result = userRepository.page(query);
        return ApiResponse.ok(new PageRes<>(
                BeanUtils.copyList(result.getRecords(), UserRes.class),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize()
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
                .orElseThrow(() -> BizException.notFound("用户"));
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
