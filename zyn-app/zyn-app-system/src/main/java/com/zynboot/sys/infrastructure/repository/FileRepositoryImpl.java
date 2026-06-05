package com.zynboot.sys.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.sys.domain.aggregate.FileAggregate;
import com.zynboot.sys.domain.repository.FileRepository;
import com.zynboot.sys.infrastructure.entity.SysFile;
import com.zynboot.sys.infrastructure.mapper.SysFileMapper;
import com.zynboot.sys.query.file.FileQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final SysFileMapper mapper;

    @Override
    public List<FileAggregate> findList(FileQuery query) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<SysFile>()
                .eq(StringUtils.hasText(query.getBizType()), SysFile::getBizType, query.getBizType())
                .eq(StringUtils.hasText(query.getBizId()), SysFile::getBizId, query.getBizId())
                .eq(StringUtils.hasText(query.getUploaderId()), SysFile::getUploaderId, query.getUploaderId())
                .like(StringUtils.hasText(query.getOriginalName()), SysFile::getOriginalName, query.getOriginalName())
                .orderByAsc(SysFile::getSortOrder)
                .orderByDesc(SysFile::getCreateTime);
        return mapper.selectList(wrapper).stream().map(FileAggregate::from).toList();
    }

    @Override
    public Optional<FileAggregate> findById(String id) {
        SysFile entity = mapper.selectById(id);
        return entity != null ? Optional.of(FileAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public Optional<FileAggregate> findByMd5(String md5) {
        if (!StringUtils.hasText(md5)) {
            return Optional.empty();
        }
        SysFile entity = mapper.selectOne(
                new LambdaQueryWrapper<SysFile>().eq(SysFile::getMd5, md5).last("LIMIT 1"));
        return entity != null ? Optional.of(FileAggregate.from(entity)) : Optional.empty();
    }

    @Override
    public void save(FileAggregate file) {
        mapper.insert(file.getEntity());
    }

    @Override
    public void delete(String id) {
        mapper.deleteById(id);
    }
}
