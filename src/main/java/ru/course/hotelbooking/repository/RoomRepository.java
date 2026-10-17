package ru.course.hotelbooking.repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.course.hotelbooking.model.Room;
import java.util.Optional;
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    boolean existsByHotelId(Long hotelId);
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
    boolean existsByHotelIdAndRoomNumberAndIdNot(Long hotelId, String roomNumber, Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}
