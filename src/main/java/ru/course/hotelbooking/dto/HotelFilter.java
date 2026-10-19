package ru.course.hotelbooking.dto;

import java.math.BigDecimal;

public record HotelFilter(Long id, String name, String advertisementTitle, String city,
                          String address, BigDecimal distanceFromCenter, BigDecimal rating, Long ratingCount) {
}
