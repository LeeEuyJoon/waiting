package com.waiting.admission_worker;

import com.waiting.common.QueueKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Set;

@Component
public class AdmissionWorker {

    private static final Logger log = LoggerFactory.getLogger(AdmissionWorker.class);

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List> admissionScript;

    @Value("${queue.capacity}")
    private int capacity;

    @Value("${admission.ttl-seconds}")
    private long ttlSeconds;

    public AdmissionWorker(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.admissionScript = RedisScript.of(new ClassPathResource("admission.lua"), List.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void admit() {
        Set<String> scheduleIds = redisTemplate.opsForSet().members(QueueKeys.activeSchedulesKey());
        if (scheduleIds == null || scheduleIds.isEmpty()) {
            return;
        }
        for (String scheduleId: scheduleIds) {
            admitForSchedule(scheduleId);
        }
    }

    private void admitForSchedule(String scheduleId) {
        String queueKey = QueueKeys.queueKey(scheduleId);
        String activeKey = QueueKeys.activeKey(scheduleId);

        long now = System.currentTimeMillis();
        long ttlMillis = ttlSeconds * 1000;

        List<String> admitted = redisTemplate.execute(
                admissionScript,
                List.of(queueKey, activeKey),
                String.valueOf(capacity),
                String.valueOf(now),
                String.valueOf(ttlMillis)
        );

        if (admitted != null && !admitted.isEmpty()) {
            String channel = QueueKeys.admissionChannel(scheduleId);
            admitted.forEach(token -> {
                log.info("Admitted: {} ({})", token, scheduleId);
                redisTemplate.convertAndSend(channel, token);
            });
        }

        cleanupIfEmpty(scheduleId, queueKey, activeKey);
    }

    private void cleanupIfEmpty(String scheduleId, String queueKey, String activeKey) {
        Long queueSize = redisTemplate.opsForZSet().size(queueKey);
        Long activeSize = redisTemplate.opsForZSet().size(activeKey);
        boolean empty = (queueSize == null || queueSize == 0) && (activeSize == null || activeSize == 0);
        if (empty) {
            redisTemplate.opsForSet().remove(QueueKeys.activeSchedulesKey(), scheduleId);
        }
    }

}
