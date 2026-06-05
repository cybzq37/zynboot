package com.zynboot.gateway.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gateway 下游服务健康检查聚合。
 */
@Component
@RequiredArgsConstructor
public class DownstreamHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Health> health() {
        Map<String, String> services = Map.of(
                "system", "http://localhost:28081",
                "demo", "http://localhost:28080"
        );

        return Flux.fromIterable(services.entrySet())
                .flatMap(e -> checkService(e.getKey(), e.getValue()))
                .collectList()
                .map(list -> {
                    Health.Builder builder = Health.up();
                    for (var entry : list) {
                        builder.withDetail(entry.getKey(), entry.getValue());
                        if ("DOWN".equals(entry.getValue())) {
                            builder.status(Status.DOWN);
                        }
                    }
                    return builder.build();
                });
    }

    private Mono<Map.Entry<String, String>> checkService(String name, String baseUrl) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/actuator/health")
                .retrieve()
                .toBodilessEntity()
                .map(resp -> (Map.Entry<String, String>) Map.entry(name, "UP"))
                .onErrorResume(e -> Mono.just(Map.entry(name, "DOWN")))
                .timeout(Duration.ofSeconds(3), Mono.just(Map.entry(name, "TIMEOUT")));
    }
}
