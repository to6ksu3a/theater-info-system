package ru.theater.info.util;

import org.springframework.data.jpa.domain.Specification;
import ru.theater.info.enumModel.*;
import ru.theater.info.model.Performance;

import java.time.LocalDateTime;
import java.util.List;

public class PerformanceSpecifications {

    public static Specification<Performance> hasTitle(String title) {
        return (root, query, cb) ->
                (title == null || title.isEmpty())
                        ? null
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Performance> hasGenre(Genre genre) {
        return (root, query, cb) ->
                genre == null
                        ? null
                        : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Performance> durationGreaterThan(int minDuration) {
        return (root, query, cb) ->
                minDuration <= 0
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("duration"), minDuration);
    }

    public static Specification<Performance> dateAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("dateTime"), startDate);
    }

    public static Specification<Performance> hasAgeRatings(List<AgeRating> ageRatings) {
        return (root, query, cb) -> {
            if (ageRatings == null || ageRatings.isEmpty()) return null;
            return root.get("ageRating").in(ageRatings);
        };
    }
}