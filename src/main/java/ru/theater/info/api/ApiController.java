package ru.theater.info.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.theater.info.enumModel.*;
import ru.theater.info.dto.PerformanceDto;
import ru.theater.info.model.Performance;
import ru.theater.info.service.PerformanceService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/performances")
public class ApiController {

    private final PerformanceService performanceService;
    @Autowired
    public ApiController(PerformanceService performanceService) { this.performanceService = performanceService; }

    @GetMapping
    @Cacheable("performance")
    public ResponseEntity<List<Performance>> getAll() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(performanceService.getAll());
    }

    @GetMapping("/{id}")
    @Cacheable(value = "performances", key="#id")
    public ResponseEntity<Performance> getById(@PathVariable Long id) {
        Performance performance = performanceService.getPerformanceById(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .eTag(createETag(performance))
                .body(performance);
    }

    @PatchMapping("/{id}")
    @CacheEvict(value = "performances", key = "#id")
    public ResponseEntity<Performance> patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Performance existing = performanceService.getPerformanceById(id);
        applyUpdates(existing, updates);
        Performance updated = performanceService.updatePerformance(id, existing);
        return ResponseEntity.ok()
                .eTag(createETag(updated))
                .body(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "performances", key = "#id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noCache())
                .build();
    }

    private String createETag(Performance performance) {
        return "\"" + performance.hashCode() + "\"";
    }

    private Performance convertToEntity(PerformanceDto dto) {
        Performance performance = new Performance();
        performance.setTitle(dto.getTitle());
        performance.setDescription(dto.getDescription());
        performance.setDateTime(dto.getDateTime());
        performance.setDuration(dto.getDuration());
        performance.setGenre(dto.getGenre());
        performance.setAgeRating(dto.getAgeRating());
        return performance;
    }

    private void applyUpdates(Performance performance, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "title": performance.setTitle((String)value); break;
                case "description": performance.setDescription((String)value); break;
                case "dateTime": performance.setDateTime((LocalDateTime)value); break;
                case "duration": performance.setDuration((Integer)value); break;
                case "genre": performance.setGenre(Genre.valueOf((String)value)); break;
                case "ageRating": performance.setAgeRating(AgeRating.valueOf((String)value)); break;
            }
        });
    }
}
