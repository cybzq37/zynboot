package com.zyn.sys.domain.repository;

import com.zyn.sys.infrastructure.entity.SysResource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    List<SysResource> findAll();

    Optional<SysResource> findById(String id);

    void save(SysResource resource);

    void update(SysResource resource);

    void delete(String id);
}
