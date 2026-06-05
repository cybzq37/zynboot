package com.zynboot.infra.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Spring Cloud Stream 消息客户端。
 * <p>
 * 通过 {@link StreamBridge} 发送消息到指定 binding，无需关心底层 Kafka 细节。
 */
@Slf4j
public class MessageClient {

    private final StreamBridge streamBridge;

    public MessageClient(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * 发送消息到指定 binding。
     *
     * @param bindingName binding 名称（如 "event-out-0"）
     * @param payload     消息体
     */
    public void send(String bindingName, Object payload) {
        boolean sent = streamBridge.send(bindingName, payload);
        if (sent) {
            log.debug("Message sent to {}: {}", bindingName, payload);
        } else {
            log.warn("Failed to send message to {}", bindingName);
        }
    }

    /**
     * 发送带 header 的消息。
     */
    public void send(String bindingName, Object payload, String headerName, Object headerValue) {
        Message<?> message = MessageBuilder.withPayload(payload)
                .setHeader(headerName, headerValue)
                .build();
        boolean sent = streamBridge.send(bindingName, message);
        if (sent) {
            log.debug("Message sent to {} with header {}={}", bindingName, headerName, headerValue);
        } else {
            log.warn("Failed to send message to {}", bindingName);
        }
    }
}
