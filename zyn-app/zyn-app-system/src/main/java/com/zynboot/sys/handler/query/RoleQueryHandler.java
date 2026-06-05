package com.zynboot.sys.handler.query;

import com.zynboot.sys.response.role.RoleRes;
import com.zynboot.sys.infrastructure.entity.SysRole;
import com.zynboot.sys.infrastructure.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色查询处理器。
 */
@Component
@RequiredArgsConstructor
public class RoleQueryHandler {

    private final SysRoleMapper roleMapper;

    public RoleRes findById(String id) {
        SysRole entity = roleMapper.selectById(id);
        return entity != null ? toRes(entity) : null;
    }

    public List<RoleRes> listAll() {
        return roleMapper.selectList(null).stream()
                .map(this::toRes)
                .collect(Collectors.toList());
    }

    public List<SysRole> getRolesByUserId(String userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    private RoleRes toRes(SysRole entity) {
        return RoleRes.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .type(entity.getType())
                .dataScope(entity.getDataScope())
                .sortOrder(entity.getSortOrder())
                .status(entity.getStatus())
                .remark(entity.getRemark())
                .build();
    }
}
