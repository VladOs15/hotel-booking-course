package ru.course.hotelbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.course.hotelbooking.dto.HotelResponse;
import ru.course.hotelbooking.exception.BadRequestException;
import ru.course.hotelbooking.mapper.HotelMapper;
import ru.course.hotelbooking.model.Hotel;
import ru.course.hotelbooking.repository.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {
    @Mock
    HotelRepository hotelRepository;
    @Mock
    RoomRepository roomRepository;
    @Mock
    HotelMapper mapper;
    @InjectMocks
    HotelService service;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setRating(new BigDecimal("0.0"));
        hotel.setRatingCount(0L);
    }

    @Test
    void firstRatingDoesNotDivideByZero() {
        when(hotelRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hotel));
        when(mapper.toResponse(hotel)).thenAnswer(inv -> response(hotel));
        HotelResponse result = service.rate(1L, 5);
        assertEquals(new BigDecimal("5.0"), result.rating());
        assertEquals(1L, result.ratingCount());
    }

    @Test
    void ratingFollowsAssignmentFormula() {
        hotel.setRating(new BigDecimal("3.0"));
        hotel.setRatingCount(2L);
        when(hotelRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hotel));
        when(mapper.toResponse(hotel)).thenAnswer(inv -> response(hotel));
        HotelResponse result = service.rate(1L, 5);
        assertEquals(new BigDecimal("4.0"), result.rating());
        assertEquals(3L, result.ratingCount());
    }

    @Test
    void invalidMarkRejected() {
        assertThrows(BadRequestException.class, () -> service.rate(1L, 6));
        verifyNoInteractions(hotelRepository);
    }

    private HotelResponse response(Hotel h) {
        return new HotelResponse(h.getId(), null, null, null, null, null, h.getRating(), h.getRatingCount());
    }
}
