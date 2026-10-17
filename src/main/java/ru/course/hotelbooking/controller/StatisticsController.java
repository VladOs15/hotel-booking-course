package ru.course.hotelbooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.course.hotelbooking.statistics.StatisticsCsvService;
import java.nio.charset.StandardCharsets;

@RestController @RequestMapping("/api/statistics") @RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsCsvService stats;
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> csv() {
        return ResponseEntity.ok()
            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.csv")
            .body(stats.export());
    }
}
