package com.waiting.api_server.queue;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/{scheduleId}/enter")
    public EnterResponse enter(@PathVariable("scheduleId") String scheduleId) {
        String token = queueService.enter(scheduleId);
        return new EnterResponse(token);
    }

    @GetMapping("/{scheduleId}/rank")
    public RankResponse rank(@PathVariable("scheduleId") String scheduleId, @RequestParam("token") String token) {
        Long rank = queueService.getRank(scheduleId, token);
        if (rank == null) {
            return new RankResponse(null, "대기열에 없음");
        }
        return new RankResponse(rank + 1, "OK");
    }

    public record EnterResponse(String token) {}
    public record RankResponse(Long rank, String message) {}
}
