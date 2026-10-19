package ru.course.hotelbooking.search;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import ru.course.hotelbooking.dto.HotelFilter;
import ru.course.hotelbooking.model.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HotelSpecifications {
    private HotelSpecifications() {
    }

    public static Specification<Hotel> byFilter(HotelFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.id() != null) p.add(cb.equal(root.get("id"), f.id()));
            if (hasText(f.name())) p.add(cb.like(cb.lower(root.get("name")), contains(f.name())));
            if (hasText(f.advertisementTitle()))
                p.add(cb.like(cb.lower(root.get("advertisementTitle")), contains(f.advertisementTitle())));
            if (hasText(f.city())) p.add(cb.like(cb.lower(root.get("city")), contains(f.city())));
            if (hasText(f.address())) p.add(cb.like(cb.lower(root.get("address")), contains(f.address())));
            if (f.distanceFromCenter() != null) p.add(cb.equal(root.get("distanceFromCenter"), f.distanceFromCenter()));
            if (f.rating() != null) p.add(cb.equal(root.get("rating"), f.rating()));
            if (f.ratingCount() != null) p.add(cb.equal(root.get("ratingCount"), f.ratingCount()));
            return cb.and(p.toArray(Predicate[]::new));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String contains(String s) {
        return "%" + s.toLowerCase(Locale.ROOT).trim() + "%";
    }
}
