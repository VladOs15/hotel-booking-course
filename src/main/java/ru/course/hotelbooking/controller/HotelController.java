package ru.course.hotelbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotels;

    @GetMapping("/{id}")
    public HotelResponse get(@PathVariable Long id) {
        return hotels.get(id);
    }

    @GetMapping("/{id}/edit")
    public HotelRequest editDto(@PathVariable Long id) {
        return hotels.getEditDto(id);
    }

    @GetMapping
    public PageResponse<HotelResponse> search(@ParameterObject @ModelAttribute HotelFilter filter,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return hotels.search(filter, page, size);
    }

    @PostMapping
    public ResponseEntity<HotelResponse> create(@Valid @RequestBody HotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotels.create(request));
    }

    @PutMapping("/{id}")
    public HotelResponse update(@PathVariable Long id, @Valid @RequestBody HotelRequest request) {
        return hotels.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        hotels.delete(id);
    }

    @PostMapping("/{id}/rating")
    public HotelResponse rating(@PathVariable Long id,
                                @Valid @RequestBody RatingRequest request) {
        return hotels.rate(id, request.mark());
    }
}
