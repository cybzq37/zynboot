package com.zynboot.sys.util;

import com.zynboot.infra.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Component
public class CacheHelper {

    private final RedisClient redisClient;

    public CacheHelper(@Nullable RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public boolean available() {
        return redisClient != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Duration ttl, Supplier<T> loader) {
        if (redisClient != null) {
            try {
                Object cached = redisClient.getObject(key);
                if (cached != null) {
                    return (T) cached;
                }
            } catch (Exception e) {
                log.debug("Redis getObject failed, falling back to loader: {}", e.getMessage());
            }
        }
        T value = loader.get();
        if (redisClient != null && value != null) {
            try {
                redisClient.putObject(key, value, ttl);
            } catch (Exception e) {
                log.debug("Redis putObject failed: {}", e.getMessage());
            }
        }
        return value;
    }

    public void evict(String... keys) {
        if (redisClient != null) {
            for (String key : keys) {
                redisClient.delete(key);
            }
        }
    }
}
