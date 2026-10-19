package ru.course.hotelbooking.mapper;

import org.mapstruct.*;
import ru.course.hotelbooking.model.Hotel;
import ru.course.hotelbooking.dto.*;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    Hotel toEntity(HotelRequest dto);

    HotelRequest toEditDto(Hotel hotel);

    HotelResponse toResponse(Hotel hotel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    void update(HotelRequest dto, @MappingTarget Hotel hotel);
}
