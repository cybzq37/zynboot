package com.zynboot.sys.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    private String parentId;
    private String code;
    private String name;
    private Integer type;
    private String path;
    private Integer sortOrder;
    private Boolean visible;
    private Integer status;
    private String remark;
}
