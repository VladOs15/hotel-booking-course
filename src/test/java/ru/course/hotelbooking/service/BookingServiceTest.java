package ru.course.hotelbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.*;
import ru.course.hotelbooking.mapper.BookingMapper;
import ru.course.hotelbooking.model.*;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.statistics.BookingCreated;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock BookingRepository bookings;
    @Mock RoomRepository rooms;
    @Mock UserService users;
    @Mock BookingMapper mapper;
    @Mock ApplicationEventPublisher events;
    @InjectMocks BookingService service;
    private final LocalDate start = LocalDate.of(2026, 11, 3);
    private final LocalDate end = LocalDate.of(2026, 11, 7);

    @Test void rejectOverlappingBooking() {
        Room room = room();
        when(rooms.findByIdForUpdate(10L)).thenReturn(Optional.of(room));
        when(bookings.existsOverlapping(10L, start, end)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> service.book(1L, request()));
        verify(bookings, never()).save(any());
        verifyNoInteractions(events);
    }
    @Test void rejectUnavailableDates() {
        Room room = room();
        room.setUnavailableDates(new HashSet<>(Set.of(LocalDate.of(2026, 11, 5))));
        when(rooms.findByIdForUpdate(10L)).thenReturn(Optional.of(room));
        assertThrows(BadRequestException.class, () -> service.book(1L, request()));
        verify(bookings, never()).save(any());
    }
    @Test void successfullyBooksAndEmitsEvent() {
        Room room = room();
        AppUser user = new AppUser(); user.setId(1L);
        when(rooms.findByIdForUpdate(10L)).thenReturn(Optional.of(room));
        when(users.entity(1L)).thenReturn(user);
        when(bookings.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0); b.setId(100L); return b;
        });
        when(mapper.toResponse(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            return new BookingResponse(b.getId(), b.getUser().getId(), b.getRoom().getId(), b.getCheckIn(), b.getCheckOut(), null);
        });
        BookingResponse result = service.book(1L, request());
        assertEquals(100L, result.id());
        ArgumentCaptor<BookingCreated> cap = ArgumentCaptor.forClass(BookingCreated.class);
        verify(events).publishEvent(cap.capture());
        assertEquals(1L, cap.getValue().userId());
        assertEquals(start, cap.getValue().checkIn());
    }
    @Test void departureMustBeAfterArrival() {
        assertThrows(BadRequestException.class, () -> service.book(1L, new BookingRequest(10L, end, start)));
        verifyNoInteractions(rooms);
    }
    private Room room() { Room r = new Room(); r.setId(10L); return r; }
    private BookingRequest request() { return new BookingRequest(10L, start, end); }
}
