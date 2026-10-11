package com.waiting.api_server.booking;

import com.waiting.common.QueueKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class BookingController {

    private final BookingRecordRepository repository;
    private final StringRedisTemplate redisTemplate;

    public BookingController(BookingRecordRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/{scheduleId}/booking")
    public BookingResponse book(@PathVariable("scheduleId") String scheduleId, @RequestParam("token") String token) {
        BookingRecord record = repository.save(new BookingRecord(scheduleId, token));
        redisTemplate.opsForZSet().remove(QueueKeys.activeKey(scheduleId), token);
        return new BookingResponse(record.getId(), "예매 완료");
    }

    public record BookingResponse(Long id, String message) {}

}
