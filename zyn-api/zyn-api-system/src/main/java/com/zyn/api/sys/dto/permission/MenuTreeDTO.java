package com.zyn.api.sys.dto.permission;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MenuTreeDTO {

    String id;
    String parentId;
    String permName;
    Integer permType;
    String path;
    Integer sort;
    Boolean visible;
    List<MenuTreeDTO> children;
}
