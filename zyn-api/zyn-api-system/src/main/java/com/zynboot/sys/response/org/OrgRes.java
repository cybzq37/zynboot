package com.zynboot.sys.response.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class OrgRes {

    String id;
    String parentId;
    String code;
    String name;
    Integer type;
    String leaderId;
    String phone;
    String email;
    Integer sortOrder;
    Integer status;
    String remark;
}
