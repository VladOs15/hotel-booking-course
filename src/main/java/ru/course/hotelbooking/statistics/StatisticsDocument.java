package ru.course.hotelbooking.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.*;

@Document(collection = "events")
@Getter
@Setter
@NoArgsConstructor
public class StatisticsDocument {
    @Id
    private String id;
    private StatisticsEventType type;
    private Long userId;
    private Long bookingId;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Instant occurredAt;

    public static StatisticsDocument from(StatisticsEvent event) {
        StatisticsDocument doc = new StatisticsDocument();
        doc.setId(event.eventId().toString());
        doc.setType(event.type());
        doc.setUserId(event.userId());
        doc.setBookingId(event.bookingId());
        doc.setRoomId(event.roomId());
        doc.setCheckIn(event.checkIn());
        doc.setCheckOut(event.checkOut());
        doc.setOccurredAt(event.occurredAt());
        return doc;
    }
}
