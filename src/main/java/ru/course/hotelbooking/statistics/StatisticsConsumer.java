package ru.course.hotelbooking.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class StatisticsConsumer {
    private final StatisticsRepository repository;
    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(StatisticsEvent event) {
        // eventId используется как _id: повторное получение Kafka не дублирует событие.
        repository.save(StatisticsDocument.from(event));
    }
}
