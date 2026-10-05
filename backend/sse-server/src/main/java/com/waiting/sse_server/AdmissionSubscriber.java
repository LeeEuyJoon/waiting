package com.waiting.sse_server;

import com.waiting.common.QueueKeys;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class AdmissionSubscriber {

    private final ReactiveRedisMessageListenerContainer container;
    private final ConnectionRegistry registry;

    public AdmissionSubscriber(ReactiveRedisMessageListenerContainer container, ConnectionRegistry registry) {
        this.container = container;
        this.registry = registry;
    }

    @PostConstruct
    public void subscribe() {
        container.receive(PatternTopic.of(QueueKeys.admissionChannelPattern()))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(registry::push);
    }
}
