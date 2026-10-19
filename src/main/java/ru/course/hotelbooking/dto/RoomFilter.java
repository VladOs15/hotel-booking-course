package ru.course.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public record RoomFilter(Long id, String title, BigDecimal minPrice, BigDecimal maxPrice,
                         Integer guestCount,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                         Long hotelId) {
}
