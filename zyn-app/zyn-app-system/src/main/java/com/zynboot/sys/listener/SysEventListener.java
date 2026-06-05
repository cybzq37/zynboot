package com.zynboot.sys.listener;

import com.zynboot.sys.event.SysEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 系统服务事件消费者。
 * <p>
 * 监听 zyn-event topic，处理跨服务事件。
 */
@Slf4j
@Component
public class SysEventListener {

    @Bean
    public Consumer<SysEvent> eventIn() {
        return event -> {
            log.info("[System] Received event: type={}, source={}, data={}",
                    event.getType(), event.getSource(), event.getData());
        };
    }
}
