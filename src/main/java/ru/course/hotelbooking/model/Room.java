package ru.course.hotelbooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "rooms", uniqueConstraints = @UniqueConstraint(name = "uq_rooms_hotel_number", columnNames = {"hotel_id", "room_number"}))
@Getter @Setter @NoArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(name = "room_number", nullable = false, length = 30)
    private String roomNumber;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "room_unavailable_dates", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "unavailable_date", nullable = false)
    private Set<LocalDate> unavailableDates = new HashSet<>();
}
