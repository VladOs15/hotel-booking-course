package ru.course.hotelbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.*;
import ru.course.hotelbooking.mapper.HotelMapper;
import ru.course.hotelbooking.model.Hotel;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.search.HotelSpecifications;
import java.math.*;

@Service @RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotels;
    private final RoomRepository rooms;
    private final HotelMapper mapper;

    @Transactional(readOnly = true)
    public HotelResponse get(Long id) { return mapper.toResponse(entity(id)); }
    @Transactional(readOnly = true)
    public HotelRequest getEditDto(Long id) { return mapper.toEditDto(entity(id)); }
    @Transactional(readOnly = true)
    public PageResponse<HotelResponse> search(HotelFilter filter, int page, int size) {
        return PageResponse.from(hotels.findAll(HotelSpecifications.byFilter(filter), Pages.of(page, size)), mapper::toResponse);
    }
    @Transactional
    public HotelResponse create(HotelRequest request) {
        Hotel hotel = mapper.toEntity(request);
        hotel.setRating(BigDecimal.ZERO.setScale(1));
        hotel.setRatingCount(0L);
        return mapper.toResponse(hotels.save(hotel));
    }
    @Transactional
    public HotelResponse update(Long id, HotelRequest request) {
        Hotel hotel = entity(id);
        mapper.update(request, hotel);
        return mapper.toResponse(hotel);
    }
    @Transactional
    public void delete(Long id) {
        Hotel hotel = entity(id);
        if (rooms.existsByHotelId(id)) throw new BadRequestException("Нельзя удалить отель, содержащий номера");
        hotels.delete(hotel);
    }
    @Transactional
    public HotelResponse rate(Long id, int mark) {
        if (mark < 1 || mark > 5) throw new BadRequestException("Оценка должна быть от 1 до 5");
        // SELECT ... FOR UPDATE исключает потерю оценок при конкурентных запросах.
        Hotel hotel = hotels.findByIdForUpdate(id)
            .orElseThrow(() -> new NotFoundException("Отель не найден: " + id));
        long count = hotel.getRatingCount();
        BigDecimal nextRating;
        if (count == 0) {
            nextRating = BigDecimal.valueOf(mark).setScale(1);
        } else {
            // Дословно по формуле из задания: (rating * count - rating + newMark) / count.
            nextRating = hotel.getRating().multiply(BigDecimal.valueOf(count))
                .subtract(hotel.getRating()).add(BigDecimal.valueOf(mark))
                .divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
        }
        hotel.setRating(nextRating);
        hotel.setRatingCount(count + 1);
        return mapper.toResponse(hotel);
    }
    public Hotel entity(Long id) {
        return hotels.findById(id).orElseThrow(() -> new NotFoundException("Отель не найден: " + id));
    }
}
