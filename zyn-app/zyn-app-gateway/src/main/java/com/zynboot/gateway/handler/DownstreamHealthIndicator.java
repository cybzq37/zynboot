package com.zynboot.gateway.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gateway 下游服务健康检查聚合。
 */
@Component
@RequiredArgsConstructor
public class DownstreamHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient.Builder webClientBuilder;

    @Value("${zyn.gateway.health.system-url:http://localhost:28081}")
    private String systemUrl;

    @Value("${zyn.gateway.health.demo-url:http://localhost:28080}")
    private String demoUrl;

    @Override
    public Mono<Health> health() {
        Map<String, String> services = new LinkedHashMap<>();
        services.put("system", systemUrl);
        services.put("demo", demoUrl);

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
