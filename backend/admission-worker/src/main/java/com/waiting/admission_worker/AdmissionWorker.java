package com.waiting.admission_worker;

import com.waiting.common.QueueKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Component
public class AdmissionWorker {

    private static final String SCHEDULE_ID = "train101";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List> admissionScript;

    @Value("${queue.capacity}")
    private int capacity;

    public AdmissionWorker(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.admissionScript = RedisScript.of(new ClassPathResource("admission.lua"), List.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void admit() {
        String queueKey = QueueKeys.queueKey(SCHEDULE_ID);
        String activeKey = QueueKeys.activeKey(SCHEDULE_ID);

        List<String> admitted = redisTemplate.execute(
                admissionScript,
                List.of(queueKey, activeKey),
                String.valueOf(capacity)
        );

        if (admitted != null && !admitted.isEmpty()) {
            admitted.forEach(token ->
                    System.out.println("Admitted: " + token));
        }
    }

}
