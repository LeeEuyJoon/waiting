package com.waiting.api_server.queue;

import com.waiting.common.QueueKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QueueService {

    public final StringRedisTemplate redisTemplate;

    public QueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String enter(String scheduleId) {
        String token = UUID.randomUUID().toString();
        String key = QueueKeys.queueKey(scheduleId);
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, token, score);
        return token;
    }

    public Long getRank(String scheduleId, String token) {
        String key = QueueKeys.queueKey(scheduleId);
        return redisTemplate.opsForZSet().rank(key, token);
    }
}
