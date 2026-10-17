package ru.course.hotelbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.*;
import ru.course.hotelbooking.mapper.RoomMapper;
import ru.course.hotelbooking.model.Room;
import ru.course.hotelbooking.repository.*;
import ru.course.hotelbooking.search.RoomSpecifications;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class RoomService {
    private final RoomRepository rooms;
    private final BookingRepository bookings;
    private final HotelService hotels;
    private final RoomMapper mapper;

    @Transactional(readOnly = true)
    public RoomResponse get(Long id) { return mapper.toResponse(entity(id)); }
    @Transactional(readOnly = true)
    public RoomRequest getEditDto(Long id) { return mapper.toEditDto(entity(id)); }
    @Transactional(readOnly = true)
    public PageResponse<RoomResponse> search(RoomFilter f, int page, int size) {
        if (f.checkIn() != null && f.checkOut() != null && !f.checkOut().isAfter(f.checkIn())) {
            throw new BadRequestException("Дата выезда должна быть позже даты заезда");
        }
        if (f.minPrice() != null && f.maxPrice() != null && f.minPrice().compareTo(f.maxPrice()) > 0) {
            throw new BadRequestException("Минимальная цена не может превышать максимальную");
        }
        return PageResponse.from(rooms.findAll(RoomSpecifications.byFilter(f), Pages.of(page, size)), mapper::toResponse);
    }
    @Transactional
    public RoomResponse create(RoomRequest request) {
        if (rooms.existsByHotelIdAndRoomNumber(request.hotelId(), request.roomNumber()))
            throw new BadRequestException("Номер с таким обозначением уже существует в отеле");
        Room room = mapper.toEntity(request);
        room.setHotel(hotels.entity(request.hotelId()));
        room.setUnavailableDates(new HashSet<>(safeDays(request.unavailableDates())));
        return mapper.toResponse(rooms.save(room));
    }
    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = rooms.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Номер не найден: " + id));
        if (rooms.existsByHotelIdAndRoomNumberAndIdNot(request.hotelId(), request.roomNumber(), id))
            throw new BadRequestException("Номер с таким обозначением уже существует в отеле");
        for (LocalDate date : safeDays(request.unavailableDates())) {
            if (bookings.existsOverlapping(id, date, date.plusDays(1)))
                throw new BadRequestException("Дата недоступности пересекается с бронированием: " + date);
        }
        mapper.update(request, room);
        room.setHotel(hotels.entity(request.hotelId()));
        room.getUnavailableDates().clear();
        room.getUnavailableDates().addAll(safeDays(request.unavailableDates()));
        return mapper.toResponse(room);
    }
    @Transactional
    public void delete(Long id) {
        Room room = entity(id);
        if (bookings.existsByRoomId(id)) throw new BadRequestException("Нельзя удалить номер с историей бронирований");
        rooms.delete(room);
    }
    public Room entity(Long id) {
        return rooms.findById(id).orElseThrow(() -> new NotFoundException("Номер не найден: " + id));
    }
    private Set<LocalDate> safeDays(Set<LocalDate> dates) {
        return dates == null ? Set.of() : dates;
    }
}
