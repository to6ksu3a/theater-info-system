package ru.theater.info.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
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
    public ApiController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    @Cacheable("performances")
    public ResponseEntity<List<Performance>> getAll() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(performanceService.getAll());
    }

    @GetMapping("/{id}")
    @Cacheable(value = "performances", key = "#id")
    public ResponseEntity<Performance> getById(@PathVariable Long id) {
        Performance performance = performanceService.getPerformanceById(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .eTag(createETag(performance))
                .body(performance);
    }

    // HEAD метод для проверки существования и получения ETag
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> headById(@PathVariable Long id) {
        Performance performance = performanceService.getPerformanceById(id);
        return ResponseEntity.ok()
                .eTag(createETag(performance))
                .build();
    }

    // OPTIONS метод для описания API
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                        HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.HEAD,
                        HttpMethod.OPTIONS)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "performances", allEntries = true)
    public ResponseEntity<Performance> create(@Valid @RequestBody PerformanceDto dto) {
        Performance performance = convertToEntity(dto);
        performanceService.savePerformance(performance);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(performance);
    }

    // Полное обновление представления
    @PutMapping("/{id}")
    @CacheEvict(value = "performances", key = "#id")
    public ResponseEntity<Performance> update(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceDto dto) {
        Performance performance = convertToEntity(dto);
        performance.setId(id);
        Performance updated = performanceService.updatePerformance(id, performance);
        return ResponseEntity.ok()
                .eTag(createETag(updated))
                .body(updated);
    }

    // Частичное обновление
    @PatchMapping("/{id}")
    @CacheEvict(value = "performances", key = "#id")
    public ResponseEntity<Performance> patch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Performance existing = performanceService.getPerformanceById(id);
        applyUpdates(existing, updates);
        Performance updated = performanceService.updatePerformance(id, existing);
        return ResponseEntity.ok()
                .eTag(createETag(updated))
                .body(updated);
    }

    // Удаление представления
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "performances", key = "#id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noCache())
                .build();
    }

    // Генерация ETag на основе хеш-кода объекта
    private String createETag(Performance performance) {
        return "\"" + performance.hashCode() + "\"";
    }

    // Конвертация DTO в Entity
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

    // Применение частичных обновлений
    private void applyUpdates(Performance performance, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    performance.setTitle((String) value);
                    break;
                case "description":
                    performance.setDescription((String) value);
                    break;
                case "dateTime":
                    performance.setDateTime((LocalDateTime) value);
                    break;
                case "duration":
                    performance.setDuration((Integer) value);
                    break;
                case "genre":
                    performance.setGenre(Genre.valueOf((String) value));
                    break;
                case "ageRating":
                    performance.setAgeRating(AgeRating.valueOf((String) value));
                    break;
            }
        });
    }
}