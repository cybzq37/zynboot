package com.zynboot.sys.event;

import com.zynboot.kit.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 系统事件类型枚举。
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventType implements IEnum<String> {

    USER_CREATED("USER_CREATED", "用户创建"),
    USER_UPDATED("USER_UPDATED", "用户更新"),
    USER_DELETED("USER_DELETED", "用户删除"),
    ROLE_CREATED("ROLE_CREATED", "角色创建"),
    ROLE_UPDATED("ROLE_UPDATED", "角色更新"),
    ROLE_DELETED("ROLE_DELETED", "角色删除"),
    PERMISSION_CHANGED("PERMISSION_CHANGED", "权限变更"),
    ORG_CREATED("ORG_CREATED", "组织创建"),
    ORG_UPDATED("ORG_UPDATED", "组织更新"),
    ORG_DELETED("ORG_DELETED", "组织删除");

    private final String code;
    private final String desc;

    EventType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
