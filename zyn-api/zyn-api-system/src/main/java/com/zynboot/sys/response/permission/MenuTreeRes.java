package com.zynboot.sys.response.permission;

import lombok.Builder;
import lombok.Value;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Value
@Jacksonized
@Builder
public class MenuTreeRes {

    String id;
    String parentId;
    String permName;
    Integer permType;
    String path;
    Integer sort;
    Boolean visible;
    List<MenuTreeRes> children;
}
