package ru.course.hotelbooking.mapper;

import org.mapstruct.*;
import ru.course.hotelbooking.model.AppUser;
import ru.course.hotelbooking.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    AppUser toEntity(UserRequest dto);

    UserEditResponse toEditDto(AppUser user);

    UserResponse toResponse(AppUser user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void update(UserUpdateRequest dto, @MappingTarget AppUser user);
}
