package com.zynboot.sys.domain.repository;

import com.zynboot.sys.domain.aggregate.PermissionAggregate;
import com.zynboot.sys.query.permission.PermissionQuery;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    List<PermissionAggregate> findList(PermissionQuery query);

    Optional<PermissionAggregate> findById(String id);

    void save(PermissionAggregate permission);

    void update(PermissionAggregate permission);

    void delete(String id);
}
