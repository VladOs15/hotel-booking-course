package ru.course.hotelbooking.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
public record RoomRequest(
    @NotBlank @Size(max = 200) String title,
    @NotBlank String description,
    @NotBlank @Size(max = 30) String roomNumber,
    @NotNull @DecimalMin("0.0") @Digits(integer = 10, fraction = 2) BigDecimal price,
    @NotNull @Min(1) Integer maxGuests,
    @NotNull @Positive Long hotelId,
    Set<@NotNull LocalDate> unavailableDates
) {}
