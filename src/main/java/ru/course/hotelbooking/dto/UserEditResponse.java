package ru.course.hotelbooking.dto;

import ru.course.hotelbooking.model.Role;

public record UserEditResponse(String username, String email, Role role) {
}
