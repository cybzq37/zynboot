package com.zynboot.sys.response.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class PermissionRes {

    String id;
    String parentId;
    String code;
    String name;
    Integer type;
    String path;
    Integer sortOrder;
    Boolean visible;
    Integer status;
    String remark;
}
