package ru.course.hotelbooking.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component @RequiredArgsConstructor
public class StatisticsPublisher {
    private final KafkaTemplate<String, StatisticsEvent> kafka;
    @Value("${app.kafka.topic}") private String topic;
    // Отправляем в Kafka только после успешного коммита PostgreSQL.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void registered(RegistrationCreated event) {
        kafka.send(topic, event.userId().toString(), StatisticsEvent.registration(event.userId()));
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void booked(BookingCreated event) {
        kafka.send(topic, event.userId().toString(), StatisticsEvent.booking(event));
    }
}
