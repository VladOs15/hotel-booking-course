package ru.course.hotelbooking.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsTests {
    @Mock
    StatisticsRepository repository;

    @Test
    void exportIncludesBothTypesOfEvents() {
        StatisticsDocument registration = StatisticsDocument.from(StatisticsEvent.registration(5L));
        StatisticsDocument booking = StatisticsDocument.from(StatisticsEvent.booking(new BookingCreated(
                9L, 5L, 17L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 3))));
        when(repository.findAllByOrderByOccurredAtAsc()).thenReturn(List.of(registration, booking));
        String csv = new String(new StatisticsCsvService(repository).export(), StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("event_id,type,user_id,booking_id,room_id,check_in,check_out,occurred_at"));
        assertTrue(csv.contains("USER_REGISTERED"));
        assertTrue(csv.contains("ROOM_BOOKED"));
        assertTrue(csv.contains("2026-11-01"));
    }

    @Test
    void consumerMapsKafkaEventToMongoDocument() {
        StatisticsEvent event = StatisticsEvent.registration(8L);
        new StatisticsConsumer(repository).consume(event);
        verify(repository).save(argThat(doc ->
                doc.getId().equals(event.eventId().toString()) && doc.getUserId().equals(8L)));
    }
}
