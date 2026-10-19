package ru.course.hotelbooking.mapper;

import org.mapstruct.*;
import ru.course.hotelbooking.model.Booking;
import ru.course.hotelbooking.dto.BookingResponse;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "userId", source = "user.id")
    BookingResponse toResponse(Booking booking);
}
