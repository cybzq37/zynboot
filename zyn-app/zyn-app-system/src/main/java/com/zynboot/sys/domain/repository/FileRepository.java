package com.zynboot.sys.domain.repository;

import com.zynboot.sys.domain.aggregate.FileAggregate;
import com.zynboot.sys.query.file.FileQuery;

import java.util.List;
import java.util.Optional;

public interface FileRepository {

    List<FileAggregate> findList(FileQuery query);

    Optional<FileAggregate> findById(String id);

    Optional<FileAggregate> findByMd5(String md5);

    void save(FileAggregate file);

    void delete(String id);
}
