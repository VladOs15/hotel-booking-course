package ru.course.hotelbooking.dto;
import ru.course.hotelbooking.model.Role;
public record UserResponse(Long id, String username, String email, Role role) {}
