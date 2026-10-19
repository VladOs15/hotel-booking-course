package ru.course.hotelbooking.dto;

import jakarta.validation.constraints.*;

public record RatingRequest(@NotNull @Min(1) @Max(5) Integer mark) {
}
