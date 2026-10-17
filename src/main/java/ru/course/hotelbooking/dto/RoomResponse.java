package ru.course.hotelbooking.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
public record RoomResponse(Long id, String title, String description, String roomNumber,
    BigDecimal price, Integer maxGuests, Long hotelId, Set<LocalDate> unavailableDates) {}
