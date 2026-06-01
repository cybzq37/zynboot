package com.zyn.sys.domain.repository;

import com.zyn.sys.domain.aggregate.UserAggregate;
import java.util.Optional;

/**
 * 用户仓储接口（领域层定义，基础设施层实现）。
 */
public interface UserRepository {

    Optional<UserAggregate> findById(String id);

    Optional<UserAggregate> findByUsername(String username);

    void save(UserAggregate user);

    void update(UserAggregate user);

    void delete(String id);
}
