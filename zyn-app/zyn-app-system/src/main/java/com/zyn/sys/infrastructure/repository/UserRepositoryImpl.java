package com.zyn.sys.infrastructure.repository;

import com.zyn.sys.domain.aggregate.UserAggregate;
import com.zyn.sys.domain.repository.UserRepository;
import com.zyn.sys.infrastructure.entity.SysUser;
import com.zyn.sys.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SysUserMapper mapper;

    @Override
    public Optional<UserAggregate> findById(String id) {
        SysUser entity = mapper.selectById(id);
        return entity != null ? Optional.of(UserAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public Optional<UserAggregate> findByUsername(String username) {
        SysUser entity = mapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        return entity != null ? Optional.of(UserAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(UserAggregate user) {
        mapper.insert(user.getEntity());
    }

    @Override
    public void update(UserAggregate user) {
        mapper.updateById(user.getEntity());
    }

    @Override
    public void delete(String id) {
        mapper.deleteById(id);
    }
}
