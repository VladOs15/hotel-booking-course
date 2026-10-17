package ru.course.hotelbooking.service;
import org.junit.jupiter.api.Test;
import ru.course.hotelbooking.exception.BadRequestException;
import static org.junit.jupiter.api.Assertions.*;
class PagesTest {
    @Test void validatesBoundaries() {
        assertThrows(BadRequestException.class, () -> Pages.of(-1, 20));
        assertThrows(BadRequestException.class, () -> Pages.of(0, 0));
        assertThrows(BadRequestException.class, () -> Pages.of(0, 101));
        assertEquals(10, Pages.of(2, 10).getPageSize());
        assertEquals(2, Pages.of(2, 10).getPageNumber());
    }
}
