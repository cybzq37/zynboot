package com.zynboot.sys.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件记录实体（物理删除，无乐观锁）。
 */
@Data
@TableName("sys_file")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String originalName;
    private String storageKey;
    private Long fileSize;
    private String contentType;
    private String extension;
    private String md5;
    private String bizType;
    private String bizId;
    private Integer sortOrder;
    private String uploaderId;
    private String remark;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
