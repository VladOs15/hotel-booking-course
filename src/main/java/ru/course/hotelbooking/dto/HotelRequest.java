package ru.course.hotelbooking.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HotelRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 300) String advertisementTitle,
        @NotBlank @Size(max = 150) String city,
        @NotBlank @Size(max = 400) String address,
        @NotNull @DecimalMin("0.0") @Digits(integer = 8, fraction = 2) BigDecimal distanceFromCenter
) {
}
