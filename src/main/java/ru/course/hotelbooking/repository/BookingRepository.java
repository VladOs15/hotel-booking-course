package ru.course.hotelbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.course.hotelbooking.model.Booking;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select (count(b) > 0) from Booking b where b.room.id = :roomId " +
            "and b.checkIn < :endDate and b.checkOut > :startDate")
    boolean existsOverlapping(@Param("roomId") Long roomId,
                              @Param("startDate") LocalDate checkIn,
                              @Param("endDate") LocalDate checkOut);

    boolean existsByRoomId(Long roomId);

    boolean existsByUserId(Long userId);
}
