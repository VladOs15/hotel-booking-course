package ru.course.hotelbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.course.hotelbooking.model.HotelPhoto;

import java.util.List;

public interface HotelPhotoRepository extends JpaRepository<HotelPhoto, Long> {
    List<HotelPhoto> findByHotelIdOrderById(Long hotelId);
}
