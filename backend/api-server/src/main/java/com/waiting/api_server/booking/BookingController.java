package com.waiting.api_server.booking;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class BookingController {

    private final BookingRecordRepository repository;

    public BookingController(BookingRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/{scheduleId}/booking")
    public BookingResponse book(@PathVariable("scheduleId") String scheduleId, @RequestParam("token") String token) {
        BookingRecord record = repository.save(new BookingRecord(scheduleId, token));
        return new BookingResponse(record.getId(), "예매 완료");
    }

    public record BookingResponse(Long id, String message) {}

}
