package com.waiting.sse_server;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sse/queue")
public class SseController {

    private final ConnectionRegistry registry;

    public SseController(ConnectionRegistry registry) {
        this.registry = registry;
    }

    @GetMapping(path = "/{scheduleId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> subscribe(@PathVariable("scheduleId") String scheduleId, @RequestParam("token") String token) {
        return registry.register(token)
                .asFlux()
                .map(msg -> ServerSentEvent.builder(msg).build())
                .doFinally(signal -> registry.unregister(token));
    }

}
