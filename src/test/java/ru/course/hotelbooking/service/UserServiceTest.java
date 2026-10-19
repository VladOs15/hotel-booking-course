package ru.course.hotelbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.BadRequestException;
import ru.course.hotelbooking.mapper.UserMapper;
import ru.course.hotelbooking.model.*;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.statistics.RegistrationCreated;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository users;
    @Mock
    BookingRepository bookings;
    @Mock
    UserMapper mapper;
    @Mock
    PasswordEncoder encoder;
    @Mock
    ApplicationEventPublisher events;
    @InjectMocks
    UserService service;

    @Test
    void adminCannotSelfRegister() {
        UserRequest request = new UserRequest("alice", "alice@example.org", "Password123!", Role.ADMIN);
        assertThrows(BadRequestException.class, () -> service.register(request));
        verifyNoInteractions(users, encoder, events);
    }

    @Test
    void usernameAlreadyExists() {
        UserRequest request = new UserRequest("alice", "alice@example.org", "Password123!", Role.USER);
        when(users.existsByUsername("alice")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    void storedPasswordIsEncodedAndStatsEventIsSent() {
        UserRequest request = new UserRequest("alice", "alice@example.org", "Password123!", Role.USER);
        AppUser user = new AppUser();
        user.setUsername("alice");
        user.setEmail("alice@example.org");
        user.setRole(Role.USER);
        when(mapper.toEntity(request)).thenReturn(user);
        when(encoder.encode("Password123!")).thenReturn("bcrypt-hash");
        when(users.save(user)).thenAnswer(inv -> {
            user.setId(8L);
            return user;
        });
        when(mapper.toResponse(user)).thenReturn(new UserResponse(8L, "alice", "alice@example.org", Role.USER));
        assertEquals(8L, service.register(request).id());
        assertEquals("bcrypt-hash", user.getPasswordHash());
        verify(events).publishEvent(new RegistrationCreated(8L));
    }
}
