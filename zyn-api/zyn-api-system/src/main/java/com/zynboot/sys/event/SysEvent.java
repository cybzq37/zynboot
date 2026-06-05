package com.zynboot.sys.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 系统事件基类。
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

    public static SysEvent of(String type, String source, String data) {
        return new SysEvent(type, source, data, Instant.now());
    }
}
