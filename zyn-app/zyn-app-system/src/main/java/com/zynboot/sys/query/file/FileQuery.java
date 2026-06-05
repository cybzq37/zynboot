package com.zynboot.sys.query.file;

import lombok.Data;

@Data
public class FileQuery {

    private String bizType;
    private String bizId;
    private String uploaderId;
    private String originalName;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
