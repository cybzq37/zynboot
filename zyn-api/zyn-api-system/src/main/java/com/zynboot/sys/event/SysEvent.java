package com.zynboot.sys.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 系统事件基类。
 * <p>
 * 通过 {@link #schemaVersion} 支持消费端版本兼容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysEvent {

    /** 事件类型。 */
    private String type;

    /** 事件来源服务。 */
    private String source;

    /** 事件数据（JSON 字符串）。 */
    private String data;

    /** 事件时间。 */
    private Instant timestamp;

    /** 事件 Schema 版本，用于消费端兼容。 */
    private Integer schemaVersion;

    public static final int CURRENT_SCHEMA_VERSION = 1;

    public static SysEvent of(String type, String source, String data) {
        return new SysEvent(type, source, data, Instant.now(), CURRENT_SCHEMA_VERSION);
    }

    public static SysEvent of(EventType type, String source, String data) {
        return new SysEvent(type.getCode(), source, data, Instant.now(), CURRENT_SCHEMA_VERSION);
    }
}
