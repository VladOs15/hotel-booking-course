package ru.course.hotelbooking.dto;
import jakarta.validation.constraints.*;
public record PhotoRequest(@NotBlank @Size(max = 2000) String url) {}
