package com.zynboot.app.controller;

import com.zynboot.infra.kafka.MessageClient;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.sys.event.SysEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Spring Cloud Stream 消息驱动演示。
 * <p>
 * 发送事件到 zyn-event topic，接收并记录。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/demo/event")
public class StreamDemoController {

    private MessageClient messageClient;
    private final CopyOnWriteArrayList<SysEvent> received = new CopyOnWriteArrayList<>();

    @Autowired(required = false)
    public void setMessageClient(MessageClient mc) {
        this.messageClient = mc;
    }

    @PostMapping("/send")
    public ApiResponse<String> send(@RequestParam String type, @RequestParam String data) {
        if (messageClient == null) {
            return ApiResponse.fail("MessageClient not configured");
        }
        SysEvent event = SysEvent.of(type, "zyn-demo", data);
        messageClient.send("event-out-0", event);
        return ApiResponse.ok("event sent: " + type);
    }

    @GetMapping("/list")
    public ApiResponse<List<SysEvent>> list() {
        return ApiResponse.ok(received);
    }

    /**
     * Spring Cloud Stream 消费者，消费 zyn-event topic 的事件。
     */
    @Component
    public static class EventConsumer {

        private final StreamDemoController controller;

        public EventConsumer(StreamDemoController controller) {
            this.controller = controller;
        }

        @Bean
        public Consumer<SysEvent> eventIn() {
            return event -> {
                log.info("Received event: type={}, source={}", event.getType(), event.getSource());
                controller.received.add(event);
                if (controller.received.size() > 100) {
                    controller.received.remove(0);
                }
            };
        }
    }
}
