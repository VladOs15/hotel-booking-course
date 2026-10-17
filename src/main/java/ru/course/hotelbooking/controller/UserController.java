package ru.course.hotelbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.security.HotelUserDetails;
import ru.course.hotelbooking.service.UserService;

@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService users;
    @PostMapping("/register") public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(users.register(request));
    }
    @PostMapping public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(users.create(request));
    }
    @GetMapping("/{id}") public UserResponse get(@PathVariable Long id, @AuthenticationPrincipal HotelUserDetails principal) {
        allowSelfOrAdmin(id, principal);
        return users.get(id);
    }
    @GetMapping("/{id}/edit") public UserEditResponse editDto(@PathVariable Long id,
                                 @AuthenticationPrincipal HotelUserDetails principal) {
        allowSelfOrAdmin(id, principal);
        return users.getEditDto(id);
    }
    @GetMapping("/by-username/{username}") public UserResponse username(@PathVariable String username,
                                 @AuthenticationPrincipal HotelUserDetails principal) {
        if (!principal.getUsername().equals(username) && !isAdmin(principal))
            throw new AccessDeniedException("Нет доступа к другому пользователю");
        return users.findByUsername(username);
    }
    @PutMapping("/{id}") public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return users.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { users.delete(id); }
    private static boolean isAdmin(HotelUserDetails principal) {
        return principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    private static void allowSelfOrAdmin(Long id, HotelUserDetails principal) {
        if (!id.equals(principal.id()) && !isAdmin(principal))
            throw new AccessDeniedException("Нет доступа к другому пользователю");
    }
}
