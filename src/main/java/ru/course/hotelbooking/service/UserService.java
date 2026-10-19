package ru.course.hotelbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.*;
import ru.course.hotelbooking.mapper.UserMapper;
import ru.course.hotelbooking.model.*;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.statistics.RegistrationCreated;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final BookingRepository bookings;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher events;

    @Transactional
    public UserResponse register(UserRequest request) {
        // Публичная регистрация НИКОГДА не может назначить роль ADMIN.
        if (request.role() != Role.USER)
            throw new BadRequestException("Публичная регистрация доступна только с ролью USER");
        return create(request);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        checkUnique(request.username(), request.email(), null);
        AppUser user = mapper.toEntity(request);
        user.setPasswordHash(encoder.encode(request.password()));
        user = users.save(user);
        events.publishEvent(new RegistrationCreated(user.getId()));
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return mapper.toResponse(entity(id));
    }

    @Transactional(readOnly = true)
    public UserEditResponse getEditDto(Long id) {
        return mapper.toEditDto(entity(id));
    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        return mapper.toResponse(users.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + username)));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        AppUser user = entity(id);
        checkUnique(request.username(), request.email(), id);
        mapper.update(request, user);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(encoder.encode(request.password()));
        }
        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        AppUser user = entity(id);
        if (bookings.existsByUserId(id)) throw new BadRequestException("Нельзя удалить пользователя с бронированиями");
        users.delete(user);
    }

    private void checkUnique(String username, String email, Long id) {
        boolean usernameTaken = id == null ? users.existsByUsername(username) : users.existsByUsernameAndIdNot(username, id);
        boolean emailTaken = id == null ? users.existsByEmail(email) : users.existsByEmailAndIdNot(email, id);
        if (usernameTaken || emailTaken) throw new BadRequestException("Логин или email уже зарегистрированы");
    }

    public AppUser entity(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }
}
