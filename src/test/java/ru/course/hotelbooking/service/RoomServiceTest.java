package ru.course.hotelbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.course.hotelbooking.dto.RoomFilter;
import ru.course.hotelbooking.exception.BadRequestException;
import ru.course.hotelbooking.mapper.RoomMapper;
import ru.course.hotelbooking.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock RoomRepository rooms;
    @Mock BookingRepository bookings;
    @Mock HotelService hotels;
    @Mock RoomMapper mapper;
    @InjectMocks RoomService service;

    @Test void oneDateOnlyDoesNotActivateAvailabilityFilter() {
        LocalDate date = LocalDate.of(2026, 11, 3);
        RoomFilter filter = new RoomFilter(null, null, null, null, null, date, null, null);
        when(rooms.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());
        service.search(filter, 0, 10);
        verify(rooms).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
    }
    @Test void reversedDatesAreRejected() {
        RoomFilter filter = new RoomFilter(null, null, null, null, null,
                LocalDate.of(2026, 11, 4), LocalDate.of(2026, 11, 2), null);
        assertThrows(BadRequestException.class, () -> service.search(filter, 0, 10));
    }
    @Test void invalidPriceRangeIsRejected() {
        RoomFilter filter = new RoomFilter(null, null, new BigDecimal("500"), new BigDecimal("100"),
                        null, null, null, null);
        assertThrows(BadRequestException.class, () -> service.search(filter, 0, 10));
    }
}
