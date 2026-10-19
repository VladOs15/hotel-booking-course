package ru.course.hotelbooking.mapper;

import org.mapstruct.*;
import ru.course.hotelbooking.model.Room;
import ru.course.hotelbooking.dto.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    Room toEntity(RoomRequest dto);

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomRequest toEditDto(Room room);

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponse toResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "unavailableDates", ignore = true)
    void update(RoomRequest dto, @MappingTarget Room room);
}
