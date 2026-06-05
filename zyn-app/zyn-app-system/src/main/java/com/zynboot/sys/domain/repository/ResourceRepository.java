package com.zynboot.sys.domain.repository;

import com.zynboot.sys.domain.aggregate.ResourceAggregate;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    List<ResourceAggregate> findAll();

    Optional<ResourceAggregate> findById(String id);

    void save(ResourceAggregate resource);

    void update(ResourceAggregate resource);

    void delete(String id);
}
