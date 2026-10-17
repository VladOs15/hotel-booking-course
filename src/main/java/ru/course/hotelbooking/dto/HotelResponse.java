package ru.course.hotelbooking.dto;
import java.math.BigDecimal;
public record HotelResponse(Long id, String name, String advertisementTitle, String city,
    String address, BigDecimal distanceFromCenter, BigDecimal rating, Long ratingCount) {}
