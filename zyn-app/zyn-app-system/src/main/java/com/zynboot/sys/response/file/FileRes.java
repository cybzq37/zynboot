package com.zynboot.sys.response.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

/**
 * 文件信息响应。
 */
@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class FileRes {

    String id;
    String originalName;
    String storageKey;
    Long fileSize;
    String contentType;
    String extension;
    String md5;
    String bizType;
    String bizId;
    Integer sortOrder;
    String uploaderId;
    String remark;
    String accessUrl;
    LocalDateTime createTime;
}
