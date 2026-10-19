package ru.course.hotelbooking.dto;

import jakarta.validation.constraints.*;
import ru.course.hotelbooking.model.Role;

public record UserRequest(
        @NotBlank @Size(min = 3, max = 100) String username,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull Role role
) {
}
