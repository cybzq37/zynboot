package com.zyn.sys.query.resource;

import lombok.Data;

@Data
public class ResourceQuery {

    private String permissionId;
    private String resName;
    private Integer resType;
    private Integer status;
}
