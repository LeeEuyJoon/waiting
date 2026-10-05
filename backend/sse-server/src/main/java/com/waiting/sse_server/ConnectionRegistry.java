package com.waiting.sse_server;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionRegistry {

    private final Map<String, Sinks.Many<String>> connections = new ConcurrentHashMap<>();

    public Sinks.Many<String> register(String token) {
        Sinks.Many<String> sink = Sinks.many().unicast()
                .onBackpressureBuffer();
        connections.put(token, sink);
        return sink;
    }

    public void unregister(String token) {
        connections.remove(token);
    }

    public void push(String token) {
        Sinks.Many<String> sink = connections.get(token);
        if (sink != null) {
            sink.tryEmitNext(token);
        }
    }

}
