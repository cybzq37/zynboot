package com.zyn.sys.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.sys.domain.aggregate.RoleAggregate;
import com.zyn.sys.domain.repository.RoleRepository;
import com.zyn.sys.infrastructure.entity.SysRole;
import com.zyn.sys.infrastructure.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final SysRoleMapper mapper;

    @Override
    public Optional<RoleAggregate> findById(String id) {
        SysRole entity = mapper.selectById(id);
        return entity != null ? Optional.of(RoleAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public Optional<RoleAggregate> findByCode(String roleCode) {
        SysRole entity = mapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        return entity != null ? Optional.of(RoleAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(RoleAggregate role) {
        mapper.insert(role.getEntity());
    }

    @Override
    public void update(RoleAggregate role) {
        mapper.updateById(role.getEntity());
    }
}
