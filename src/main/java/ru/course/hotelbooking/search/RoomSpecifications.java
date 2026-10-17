package ru.course.hotelbooking.search;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ru.course.hotelbooking.dto.RoomFilter;
import ru.course.hotelbooking.model.Booking;
import ru.course.hotelbooking.model.Room;
import java.time.LocalDate;
import java.util.*;

public final class RoomSpecifications {
    private RoomSpecifications() {}
    public static Specification<Room> byFilter(RoomFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.id() != null) p.add(cb.equal(root.get("id"), f.id()));
            if (f.title() != null && !f.title().isBlank()) {
                p.add(cb.like(cb.lower(root.get("title")), "%" + f.title().trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (f.minPrice() != null) p.add(cb.greaterThanOrEqualTo(root.get("price"), f.minPrice()));
            if (f.maxPrice() != null) p.add(cb.lessThanOrEqualTo(root.get("price"), f.maxPrice()));
            if (f.guestCount() != null) p.add(cb.greaterThanOrEqualTo(root.get("maxGuests"), f.guestCount()));
            if (f.hotelId() != null) p.add(cb.equal(root.get("hotel").get("id"), f.hotelId()));
            if (f.checkIn() != null && f.checkOut() != null) {
                // Пересечение периодов [checkIn, checkOut): A.start < B.end && A.end > B.start.
                Subquery<Long> reservations = query.subquery(Long.class);
                Root<Booking> booking = reservations.from(Booking.class);
                reservations.select(cb.literal(1L)).where(
                    cb.equal(booking.get("room").get("id"), root.get("id")),
                    cb.lessThan(booking.get("checkIn"), f.checkOut()),
                    cb.greaterThan(booking.get("checkOut"), f.checkIn())
                );
                p.add(cb.not(cb.exists(reservations)));
                // Помимо броней, исключаем вручную закрытые даты номера.
                Subquery<Long> unavailable = query.subquery(Long.class);
                Root<Room> correlatedRoom = unavailable.correlate(root);
                SetJoin<Room, LocalDate> day = correlatedRoom.joinSet("unavailableDates");
                unavailable.select(cb.literal(1L)).where(
                    cb.greaterThanOrEqualTo(day, f.checkIn()),
                    cb.lessThan(day, f.checkOut())
                );
                p.add(cb.not(cb.exists(unavailable)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }
}
