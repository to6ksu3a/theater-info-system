package ru.theater.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.theater.info.service.StatsService;

@Controller
@RequestMapping("/statistics")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public String showStatistics(Model model) {
        long totalUsers = statsService.getTotalUsers();
        var perMonth = statsService.getPerformancesPerMonth();

        long maxPerMonth = perMonth.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(1);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("monthLabels", perMonth.keySet());
        model.addAttribute("monthData", perMonth.values());
        model.addAttribute("maxPerMonth", maxPerMonth);

        return "statistics";
    }
}