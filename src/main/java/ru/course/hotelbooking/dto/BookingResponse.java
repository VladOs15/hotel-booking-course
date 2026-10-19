package ru.course.hotelbooking.dto;

import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(Long id, Long userId, Long roomId, LocalDate checkIn,
                              LocalDate checkOut, Instant createdAt) {
}
