package com.example.pastebin.paste.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PasteCacheService {
    private static final Duration TTL = Duration.ofHours(1);

    private final StringRedisTemplate redisTemplate;

    public PasteCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value, TTL);
    }
}
