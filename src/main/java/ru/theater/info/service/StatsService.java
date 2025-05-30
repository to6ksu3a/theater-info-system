package ru.theater.info.service;

import org.springframework.stereotype.Service;
import ru.theater.info.repository.UserRepository;
import ru.theater.info.repository.PerformanceRepository;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;

    public StatsService(UserRepository userRepository,
                        PerformanceRepository performanceRepository) {
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public Map<String, Long> getPerformancesPerMonth() {
        YearMonth now = YearMonth.now();
        List<YearMonth> months =
                java.util.stream.IntStream.rangeClosed(-2, 9)
                        .mapToObj(now::plusMonths)
                        .toList();

        Map<String, Long> result = new LinkedHashMap<>();
        for (YearMonth ym : months) {
            long count = performanceRepository.findAll().stream()
                    .filter(p -> YearMonth.from(p.getDateTime()).equals(ym))
                    .count();
            result.put(ym.getMonth().name().substring(0,3) + " " + ym.getYear(), count);
        }
        return result;
    }
}