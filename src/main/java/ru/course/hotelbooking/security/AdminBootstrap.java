package ru.course.hotelbooking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.model.*;
import ru.course.hotelbooking.repository.UserRepository;

@Component @RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.admin.username:}") private String username;
    @Value("${app.admin.email:admin@example.com}") private String email;
    @Value("${app.admin.password:}") private String password;

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return;
        if (password.length() < 12) throw new IllegalStateException("APP_ADMIN_PASSWORD должен быть не короче 12 символов");
        if (users.existsByUsername(username)) {
            AppUser existing = users.findByUsername(username).orElseThrow();
            if (existing.getRole() != Role.ADMIN) {
                throw new IllegalStateException("APP_ADMIN_USERNAME занят обычным пользователем");
            }
            return;
        }
        if (users.existsByEmail(email)) throw new IllegalStateException("APP_ADMIN_EMAIL уже занят");
        AppUser admin = new AppUser();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(encoder.encode(password));
        admin.setRole(Role.ADMIN);
        users.save(admin);
    }
}
