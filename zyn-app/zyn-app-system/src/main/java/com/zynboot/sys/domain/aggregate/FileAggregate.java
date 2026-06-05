package com.zynboot.sys.domain.aggregate;

import com.zynboot.sys.infrastructure.entity.SysFile;

/**
 * 文件聚合根。
 */
public class FileAggregate {

    private final SysFile entity;

    private FileAggregate(SysFile entity) {
        this.entity = entity;
    }

    public static FileAggregate from(SysFile entity) {
        return new FileAggregate(entity);
    }

    public static FileAggregate create(String originalName, String storageKey, long fileSize,
                                       String contentType, String extension, String md5,
                                       String bizType, String bizId, String uploaderId) {
        SysFile file = new SysFile();
        file.setOriginalName(originalName);
        file.setStorageKey(storageKey);
        file.setFileSize(fileSize);
        file.setContentType(contentType);
        file.setExtension(extension);
        file.setMd5(md5);
        file.setBizType(bizType);
        file.setBizId(bizId);
        file.setSortOrder(0);
        file.setUploaderId(uploaderId);
        return new FileAggregate(file);
    }

    public SysFile getEntity() {
        return entity;
    }

    public String getId() {
        return entity.getId();
    }

    public String getStorageKey() {
        return entity.getStorageKey();
    }

    public String getOriginalName() {
        return entity.getOriginalName();
    }

    public String getMd5() {
        return entity.getMd5();
    }

    public void setSortOrder(int sortOrder) {
        entity.setSortOrder(sortOrder);
    }

    public void setRemark(String remark) {
        entity.setRemark(remark);
    }
}
