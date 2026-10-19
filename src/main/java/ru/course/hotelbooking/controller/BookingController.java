package ru.course.hotelbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.security.HotelUserDetails;
import ru.course.hotelbooking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookings;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @AuthenticationPrincipal HotelUserDetails principal,
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookings.book(principal.id(), request));
    }

    @GetMapping
    public PageResponse<BookingResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return bookings.list(page, size);
    }
}
