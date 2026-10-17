package ru.course.hotelbooking.statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface StatisticsRepository extends MongoRepository<StatisticsDocument, String> {
    List<StatisticsDocument> findAllByOrderByOccurredAtAsc();
}
