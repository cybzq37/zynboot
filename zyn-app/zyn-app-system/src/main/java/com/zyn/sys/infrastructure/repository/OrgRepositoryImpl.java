package com.zyn.sys.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.sys.domain.aggregate.OrgAggregate;
import com.zyn.sys.domain.repository.OrgRepository;
import com.zyn.sys.infrastructure.entity.SysOrganization;
import com.zyn.sys.infrastructure.mapper.SysOrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrgRepositoryImpl implements OrgRepository {

    private final SysOrganizationMapper mapper;

    @Override
    public Optional<OrgAggregate> findById(String id) {
        SysOrganization entity = mapper.selectById(id);
        return entity != null ? Optional.of(OrgAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public Optional<OrgAggregate> findByCode(String orgCode) {
        SysOrganization entity = mapper.selectOne(
                new LambdaQueryWrapper<SysOrganization>().eq(SysOrganization::getOrgCode, orgCode));
        return entity != null ? Optional.of(OrgAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(OrgAggregate org) {
        mapper.insert(org.getEntity());
    }

    @Override
    public void update(OrgAggregate org) {
        mapper.updateById(org.getEntity());
    }

    @Override
    public void delete(String id) {
        mapper.deleteById(id);
    }
}
