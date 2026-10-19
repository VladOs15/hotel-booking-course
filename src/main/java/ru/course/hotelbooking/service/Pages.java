package ru.course.hotelbooking.service;

import org.springframework.data.domain.*;
import ru.course.hotelbooking.exception.BadRequestException;

public final class Pages {
    private Pages() {
    }

    public static Pageable of(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException("page >= 0; size должен быть от 1 до 100");
        }
        return PageRequest.of(page, size, Sort.by("id").ascending());
    }
}
