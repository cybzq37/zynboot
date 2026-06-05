package com.zynboot.sys.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_organization")
public class SysOrganization extends BaseEntity {

    private String parentId;
    private String code;
    private String name;
    private Integer type;
    private String leaderId;
    private String phone;
    private String email;
    private Integer sortOrder;
    private Integer status;
    private String remark;
}
