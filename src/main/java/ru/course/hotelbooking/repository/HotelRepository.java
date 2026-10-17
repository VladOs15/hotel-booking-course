package ru.course.hotelbooking.repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.course.hotelbooking.model.Hotel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Hotel h where h.id = :id")
    Optional<Hotel> findByIdForUpdate(@Param("id") Long id);
}
