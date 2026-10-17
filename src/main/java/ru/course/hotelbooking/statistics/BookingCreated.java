package ru.course.hotelbooking.statistics;
import java.time.LocalDate;
public record BookingCreated(Long bookingId, Long userId, Long roomId,
                             LocalDate checkIn, LocalDate checkOut) {}
