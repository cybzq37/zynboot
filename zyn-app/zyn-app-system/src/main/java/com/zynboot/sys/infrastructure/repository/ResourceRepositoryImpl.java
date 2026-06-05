package com.zynboot.sys.infrastructure.repository;

import com.zynboot.sys.domain.aggregate.ResourceAggregate;
import com.zynboot.sys.domain.repository.ResourceRepository;
import com.zynboot.sys.infrastructure.entity.SysResource;
import com.zynboot.sys.infrastructure.mapper.SysResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResourceRepositoryImpl implements ResourceRepository {

    private final SysResourceMapper mapper;

    @Override
    public List<ResourceAggregate> findAll() {
        return mapper.selectList(null).stream().map(ResourceAggregate::from).toList();
    }

    @Override
    public Optional<ResourceAggregate> findById(String id) {
        SysResource entity = mapper.selectById(id);
        return entity != null ? Optional.of(ResourceAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(ResourceAggregate resource) {
        mapper.insert(resource.getEntity());
    }

    @Override
    public void update(ResourceAggregate resource) {
        mapper.updateById(resource.getEntity());
    }

    @Override
    public void delete(String id) {
        mapper.deleteById(id);
    }
}
