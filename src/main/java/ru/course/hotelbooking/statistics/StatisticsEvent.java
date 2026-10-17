package ru.course.hotelbooking.statistics;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StatisticsEvent(UUID eventId, StatisticsEventType type, Long userId, Long bookingId,
           Long roomId, LocalDate checkIn, LocalDate checkOut, Instant occurredAt) {
    public static StatisticsEvent registration(Long userId) {
        return new StatisticsEvent(UUID.randomUUID(), StatisticsEventType.USER_REGISTERED,
                userId, null, null, null, null, Instant.now());
    }
    public static StatisticsEvent booking(BookingCreated event) {
        return new StatisticsEvent(UUID.randomUUID(), StatisticsEventType.ROOM_BOOKED,
                event.userId(), event.bookingId(), event.roomId(), event.checkIn(),
                event.checkOut(), Instant.now());
    }
}
