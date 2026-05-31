package com.zyn.api.sys.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    String id;
    String parentId;
    String permCode;
    String permName;
    Integer permType;
    String path;
    Integer sort;
    Boolean visible;
    Integer status;
}
