package ru.course.hotelbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.service.RoomService;

@RestController @RequestMapping("/api/rooms") @RequiredArgsConstructor
public class RoomController {
    private final RoomService rooms;
    @GetMapping("/{id}") public RoomResponse get(@PathVariable Long id) { return rooms.get(id); }
    @GetMapping("/{id}/edit") public RoomRequest editDto(@PathVariable Long id) { return rooms.getEditDto(id); }
    @GetMapping public PageResponse<RoomResponse> search(@ModelAttribute RoomFilter filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) { return rooms.search(filter, page, size); }
    @PostMapping public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rooms.create(request));
    }
    @PutMapping("/{id}") public RoomResponse update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return rooms.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { rooms.delete(id); }
}
