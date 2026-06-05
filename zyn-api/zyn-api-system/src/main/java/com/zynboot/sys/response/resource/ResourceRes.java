package com.zynboot.sys.response.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class ResourceRes {

    String id;
    String permissionId;
    String name;
    Integer type;
    String requestMethod;
    String requestPath;
    Integer status;
    String remark;
}
