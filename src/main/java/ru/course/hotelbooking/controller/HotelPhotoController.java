package ru.course.hotelbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.service.HotelPhotoService;
import java.util.List;

@RestController @RequestMapping("/api/hotels/{hotelId}/photos") @RequiredArgsConstructor
public class HotelPhotoController {
    private final HotelPhotoService photos;
    @GetMapping public List<PhotoResponse> list(@PathVariable Long hotelId) { return photos.list(hotelId); }
    @PostMapping public ResponseEntity<PhotoResponse> create(@PathVariable Long hotelId,
                                                    @Valid @RequestBody PhotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(photos.create(hotelId, request));
    }
    @DeleteMapping("/{photoId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long hotelId, @PathVariable Long photoId) { photos.delete(hotelId, photoId); }
}
