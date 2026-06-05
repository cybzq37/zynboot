package com.zynboot.sys.response.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * 角色信息响应（不可变）。
 */
@Value
@Jacksonized
@Builder
@AllArgsConstructor
public class RoleRes {

    String id;
    String code;
    String name;
    Integer type;
    Integer sortOrder;
    Integer status;
    Integer dataScope;
    String remark;
}
