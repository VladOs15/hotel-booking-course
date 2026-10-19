package ru.course.hotelbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.*;
import ru.course.hotelbooking.mapper.BookingMapper;
import ru.course.hotelbooking.model.*;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.statistics.BookingCreated;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookings;
    private final RoomRepository rooms;
    private final UserService users;
    private final BookingMapper mapper;
    private final ApplicationEventPublisher events;

    @Transactional
    public BookingResponse book(Long userId, BookingRequest request) {
        validateDates(request.checkIn(), request.checkOut());
        // Пессимистическая блокировка сериализует конкурирующие брони одного номера.
        Room room = rooms.findByIdForUpdate(request.roomId())
                .orElseThrow(() -> new NotFoundException("Номер не найден: " + request.roomId()));
        if (bookings.existsOverlapping(room.getId(), request.checkIn(), request.checkOut())) {
            throw new BadRequestException("Номер уже забронирован на выбранные даты");
        }
        boolean blocked = room.getUnavailableDates().stream()
                .anyMatch(date -> !date.isBefore(request.checkIn()) && date.isBefore(request.checkOut()));
        if (blocked) throw new BadRequestException("Номер недоступен на одну из выбранных дат");
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(users.entity(userId));
        booking.setCheckIn(request.checkIn());
        booking.setCheckOut(request.checkOut());
        booking = bookings.save(booking);
        events.publishEvent(new BookingCreated(booking.getId(), userId, room.getId(),
                request.checkIn(), request.checkOut()));
        return mapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> list(int page, int size) {
        return PageResponse.from(bookings.findAll(Pages.of(page, size)), mapper::toResponse);
    }

    private static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Дата выезда должна быть позже даты заезда");
        }
    }
}
