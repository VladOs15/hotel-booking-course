package ru.course.hotelbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.hotelbooking.dto.*;
import ru.course.hotelbooking.exception.NotFoundException;
import ru.course.hotelbooking.model.HotelPhoto;
import ru.course.hotelbooking.repository.HotelPhotoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelPhotoService {
    private final HotelPhotoRepository photos;
    private final HotelService hotels;

    @Transactional(readOnly = true)
    public List<PhotoResponse> list(Long hotelId) {
        hotels.entity(hotelId);
        return photos.findByHotelIdOrderById(hotelId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public PhotoResponse create(Long hotelId, PhotoRequest request) {
        HotelPhoto photo = new HotelPhoto();
        photo.setHotel(hotels.entity(hotelId));
        photo.setUrl(request.url());
        return toResponse(photos.save(photo));
    }

    @Transactional
    public void delete(Long hotelId, Long photoId) {
        HotelPhoto photo = photos.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Фотография не найдена"));
        if (!photo.getHotel().getId().equals(hotelId)) throw new NotFoundException("Фотография не найдена в отеле");
        photos.delete(photo);
    }

    private PhotoResponse toResponse(HotelPhoto photo) {
        return new PhotoResponse(photo.getId(), photo.getHotel().getId(), photo.getUrl());
    }
}
