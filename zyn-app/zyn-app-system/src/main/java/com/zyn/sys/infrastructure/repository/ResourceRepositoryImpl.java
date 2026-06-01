package com.zyn.sys.infrastructure.repository;

import com.zyn.sys.domain.repository.ResourceRepository;
import com.zyn.sys.infrastructure.entity.SysResource;
import com.zyn.sys.infrastructure.mapper.SysResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResourceRepositoryImpl implements ResourceRepository {

    private final SysResourceMapper mapper;

    @Override
    public List<SysResource> findAll() {
        return mapper.selectList(null);
    }

    @Override
    public Optional<SysResource> findById(String id) {
        SysResource entity = mapper.selectById(id);
        return Optional.ofNullable(entity);
    }

    @Override
    public void save(SysResource resource) {
        mapper.insert(resource);
    }

    @Override
    public void update(SysResource resource) {
        mapper.updateById(resource);
    }

    @Override
    public void delete(String id) {
        mapper.deleteById(id);
    }
}
