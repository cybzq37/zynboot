package com.zyn.sys.query.permission;

import lombok.Data;

@Data
public class PermissionQuery {

    private String permName;
    private Integer permType;
    private Integer status;
}
