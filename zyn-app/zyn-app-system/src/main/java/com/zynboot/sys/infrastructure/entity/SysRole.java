package com.zynboot.sys.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private String code;
    private String name;
    private Integer type;
    private Integer sortOrder;
    private Integer status;
    private Integer dataScope;
    private String remark;
}
