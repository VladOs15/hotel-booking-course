package ru.course.hotelbooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity @Table(name = "hotels")
@Getter @Setter @NoArgsConstructor
public class Hotel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(name = "advertisement_title", nullable = false, length = 300)
    private String advertisementTitle;
    @Column(nullable = false, length = 150)
    private String city;
    @Column(nullable = false, length = 400)
    private String address;
    @Column(name = "distance_from_center", nullable = false, precision = 10, scale = 2)
    private BigDecimal distanceFromCenter;
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO.setScale(1);
    @Column(name = "rating_count", nullable = false)
    private Long ratingCount = 0L;
}
