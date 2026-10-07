package com.waiting.api_server.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {
}
