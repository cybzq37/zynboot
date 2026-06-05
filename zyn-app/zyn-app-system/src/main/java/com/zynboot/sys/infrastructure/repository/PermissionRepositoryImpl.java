package com.zynboot.sys.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.sys.domain.aggregate.PermissionAggregate;
import com.zynboot.sys.domain.repository.PermissionRepository;
import com.zynboot.sys.infrastructure.entity.SysPermission;
import com.zynboot.sys.infrastructure.entity.SysRolePermission;
import com.zynboot.sys.infrastructure.mapper.SysPermissionMapper;
import com.zynboot.sys.infrastructure.mapper.SysRolePermissionMapper;
import com.zynboot.sys.query.permission.PermissionQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final SysPermissionMapper mapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionAggregate> findList(PermissionQuery query) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<SysPermission>()
                .like(StringUtils.hasText(query.getName()), SysPermission::getName, query.getName())
                .eq(query.getType() != null, SysPermission::getType, query.getType())
                .eq(query.getStatus() != null, SysPermission::getStatus, query.getStatus())
                .orderByAsc(SysPermission::getSortOrder);
        return mapper.selectList(wrapper).stream().map(PermissionAggregate::from).toList();
    }

    @Override
    public Optional<PermissionAggregate> findById(String id) {
        SysPermission entity = mapper.selectById(id);
        return entity != null ? Optional.of(PermissionAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(PermissionAggregate permission) {
        mapper.insert(permission.getEntity());
    }

    @Override
    public void update(PermissionAggregate permission) {
        mapper.updateById(permission.getEntity());
    }

    @Override
    @Transactional
    public void delete(String id) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getPermissionId, id));
        mapper.deleteById(id);
    }
}
