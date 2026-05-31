package com.zyn.sys.handler.query;

import com.zyn.sys.response.role.RoleRes;
import com.zyn.sys.infrastructure.entity.SysRole;
import com.zyn.sys.infrastructure.entity.SysUserRole;
import com.zyn.sys.infrastructure.mapper.SysRoleMapper;
import com.zyn.sys.infrastructure.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private final SysUserRoleMapper userRoleMapper;

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
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        return userRoles.stream()
                .map(ur -> roleMapper.selectById(ur.getRoleId()))
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    private RoleRes toRes(SysRole entity) {
        RoleRes res = new RoleRes();
        res.setId(entity.getId());
        res.setRoleCode(entity.getRoleCode());
        res.setRoleName(entity.getRoleName());
        res.setRoleType(entity.getRoleType());
        res.setDataScope(entity.getDataScope());
        res.setSort(entity.getSort());
        res.setStatus(entity.getStatus());
        res.setRemark(entity.getRemark());
        return res;
    }
}
