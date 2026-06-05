package com.zynboot.sys.handler.query;

import com.zynboot.infra.storage.service.StorageService;
import com.zynboot.sys.domain.aggregate.FileAggregate;
import com.zynboot.sys.domain.repository.FileRepository;
import com.zynboot.sys.infrastructure.entity.SysFile;
import com.zynboot.sys.query.file.FileQuery;
import com.zynboot.sys.response.PageRes;
import com.zynboot.sys.response.file.FileRes;
import com.zynboot.kit.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileQueryHandler {

    private final FileRepository fileRepository;
    private final StorageService storageService;

    public PageRes<FileRes> page(FileQuery query) {
        List<FileAggregate> all = fileRepository.findList(query);
        int total = all.size();
        int offset = PaginationUtils.offset(query.getPageNum(), query.getPageSize());
        int end = Math.min(offset + query.getPageSize(), total);
        List<FileRes> records = (offset < total ? all.subList(offset, end) : List.<FileAggregate>of())
                .stream().map(this::toRes).toList();
        return new PageRes<>(records, total, query.getPageNum(), query.getPageSize());
    }

    public FileRes findById(String id) {
        return fileRepository.findById(id).map(this::toRes).orElse(null);
    }

    private FileRes toRes(FileAggregate agg) {
        SysFile entity = agg.getEntity();
        return FileRes.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalName())
                .storageKey(entity.getStorageKey())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .extension(entity.getExtension())
                .md5(entity.getMd5())
                .bizType(entity.getBizType())
                .bizId(entity.getBizId())
                .sortOrder(entity.getSortOrder())
                .uploaderId(entity.getUploaderId())
                .remark(entity.getRemark())
                .accessUrl(storageService.getAccessUrl(entity.getStorageKey()))
                .createTime(entity.getCreateTime())
                .build();
    }
}
