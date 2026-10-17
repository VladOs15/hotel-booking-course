package ru.course.hotelbooking.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record BookingRequest(@NotNull @Positive Long roomId,
    @NotNull LocalDate checkIn, @NotNull LocalDate checkOut) {}
