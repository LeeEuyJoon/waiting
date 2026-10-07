package com.waiting.api_server.booking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class BookingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleId;
    private String token;
    private Instant bookedAt;

    protected BookingRecord() {}

    public BookingRecord(String scheduleId, String token) {
        this.scheduleId = scheduleId;
        this.token = token;
        this.bookedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getToken() {
        return token;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

}
