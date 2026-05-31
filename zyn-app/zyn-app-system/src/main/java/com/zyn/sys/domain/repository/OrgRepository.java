package com.zyn.sys.domain.repository;

import com.zyn.sys.domain.aggregate.OrgAggregate;
import java.util.Optional;

/**
 * 组织仓储接口。
 */
public interface OrgRepository {

    Optional<OrgAggregate> findById(String id);

    Optional<OrgAggregate> findByCode(String orgCode);

    void save(OrgAggregate org);

    void update(OrgAggregate org);
}
