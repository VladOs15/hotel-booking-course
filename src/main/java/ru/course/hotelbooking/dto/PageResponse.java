package ru.course.hotelbooking.dto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;
public record PageResponse<T>(List<T> content, long totalElements, int totalPages, int page, int size) {
    public static <T, S> PageResponse<T> from(Page<S> result, Function<S,T> mapper) {
        return new PageResponse<>(result.map(mapper).getContent(), result.getTotalElements(),
                                  result.getTotalPages(), result.getNumber(), result.getSize());
    }
}
