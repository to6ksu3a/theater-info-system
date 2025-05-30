package ru.theater.info.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.theater.info.enumModel.AgeRating;
import ru.theater.info.enumModel.Genre;
import ru.theater.info.util.PerformanceSpecifications;
import ru.theater.info.model.Performance;
import ru.theater.info.repository.PerformanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }


    public List<Performance> getAll() {return performanceRepository.findAll();}

    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такая постановка не найдена"));
    }

    @Transactional
    public Performance updatePerformance(Long id, Performance updatedPerformance) {
        Performance existing = getPerformanceById(id);
        existing.setTitle(updatedPerformance.getTitle());
        existing.setDescription(updatedPerformance.getDescription());
        existing.setDateTime(updatedPerformance.getDateTime());
        existing.setDuration(updatedPerformance.getDuration());
        existing.setGenre(updatedPerformance.getGenre());
        existing.setAgeRating(updatedPerformance.getAgeRating());
        return performanceRepository.save(existing);
    }

    @Transactional
    public void savePerformance(Performance performance) {
        validatePerformance(performance);
        performanceRepository.save(performance);
    }

    @Transactional
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }

    private void validatePerformance(Performance performance) {
        if (performance.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата постановки не может быть раньше текущей");
        }

        if (performance.getDuration() <= 0) {
            throw new IllegalArgumentException("Неправильная длительность постановки");
        }
    }

    public List<Performance> getFilteredAndSorted(
            String title,
            Genre genre,
            int minDuration,
            LocalDate startDate,
            List<AgeRating> ageRatings,
            String sortBy,
            String direction) {

        Specification<Performance> spec = buildSpecification(title, genre, minDuration, startDate, ageRatings);
        Sort sort = buildSort(sortBy, direction);

        List<Performance> performances = performanceRepository.findAll(spec, sort);

        if ("genre".equalsIgnoreCase(sortBy)) {
            Comparator<Performance> comparator = Comparator.comparing(p -> p.getGenre().getDisplayName());
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            performances.sort(comparator);
        } else if ("ageRating".equalsIgnoreCase(sortBy)) {
            Comparator<Performance> comparator = Comparator.comparingInt(p -> p.getAgeRating().getValue());
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            performances.sort(comparator);
        }

        return performances;
    }

    private Specification<Performance> buildSpecification(
            String title,
            Genre genre,
            int minDuration,
            LocalDate startDate,
            List<AgeRating> ageRatings) {

        return Specification.where(PerformanceSpecifications.hasTitle(title))
                .and(PerformanceSpecifications.hasGenre(genre))
                .and(PerformanceSpecifications.durationGreaterThan(minDuration))
                .and(PerformanceSpecifications.dateAfter(
                        startDate != null ? startDate.atStartOfDay() : null))
                .and(PerformanceSpecifications.hasAgeRatings(ageRatings));
    }

    private Sort buildSort(String sortBy, String direction) {
        List<String> allowedFields = List.of("title", "dateTime", "genre", "duration", "ageRating");
        String validSortBy = allowedFields.contains(sortBy) ? sortBy : "dateTime";

        Sort.Direction sortDirection = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        }

        return Sort.by(sortDirection, validSortBy);
    }


}
