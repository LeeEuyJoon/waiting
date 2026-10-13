package com.waiting.admission_worker;

import com.waiting.common.QueueKeys;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@Configuration
public class MetricsConfig {

    private static final String SCHEDULE_ID = "train101";

    private final MeterRegistry meterRegistry;
    private final StringRedisTemplate redisTemplate;

    public MetricsConfig(MeterRegistry meterRegistry, StringRedisTemplate redisTemplate) {
        this.meterRegistry = meterRegistry;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void registerGauges() {
        Gauge.builder("waiting.queue.size", redisTemplate, this::queueSize)
                .description("현재 대기 중인 인원 수")
                .register(meterRegistry);

        Gauge.builder("waiting.active.count", redisTemplate, this::activeCount)
                .description("현재 활성 입장자 수")
                .register(meterRegistry);
    }

    private double queueSize(StringRedisTemplate template) {
        Set<String> scheduleIds = template.opsForSet().members(QueueKeys.activeSchedulesKey());
        if (scheduleIds == null) return 0.0;
        return scheduleIds.stream()
                .mapToLong(id -> {
                    Long size = template.opsForZSet().size(QueueKeys.queueKey(id));
                    return size != null ? size : 0L;
                })
                .sum();
    }

    private double activeCount(StringRedisTemplate template) {
        Long size = template.opsForZSet().size(QueueKeys.activeKey(SCHEDULE_ID));
        return size != null ? size.doubleValue() : 0.0;
    }

}
