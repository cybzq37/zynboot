package com.zyn.sys.domain.repository;

import com.zyn.sys.domain.aggregate.RoleAggregate;
import com.zyn.sys.query.role.RoleQuery;
import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口。
 */
public interface RoleRepository {

    Optional<RoleAggregate> findById(String id);

    Optional<RoleAggregate> findByCode(String roleCode);

    List<RoleAggregate> findList(RoleQuery query);

    void save(RoleAggregate role);

    void update(RoleAggregate role);

    void delete(String id);
}
