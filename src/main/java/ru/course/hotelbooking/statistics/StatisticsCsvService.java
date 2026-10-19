package ru.course.hotelbooking.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsCsvService {
    private final StatisticsRepository repository;

    public byte[] export() {
        List<StatisticsDocument> events = repository.findAllByOrderByOccurredAtAsc();
        StringBuilder csv = new StringBuilder("event_id,type,user_id,booking_id,room_id,check_in,check_out,occurred_at\r\n");
        for (StatisticsDocument event : events) {
            csv.append(field(event.getId())).append(',')
                    .append(field(event.getType())).append(',')
                    .append(field(event.getUserId())).append(',')
                    .append(field(event.getBookingId())).append(',')
                    .append(field(event.getRoomId())).append(',')
                    .append(field(event.getCheckIn())).append(',')
                    .append(field(event.getCheckOut())).append(',')
                    .append(field(event.getOccurredAt())).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String field(Object value) {
        if (value == null) return "";
        String text = value.toString();
        if (text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
