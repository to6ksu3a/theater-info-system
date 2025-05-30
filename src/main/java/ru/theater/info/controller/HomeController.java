package ru.theater.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.theater.info.enumModel.AgeRating;
import ru.theater.info.enumModel.Genre;
import ru.theater.info.model.Performance;
import ru.theater.info.service.PerformanceService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final PerformanceService performanceService;

    public HomeController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping
    public String home(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Genre genre,
            @RequestParam(defaultValue = "0") int minDuration,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) List<AgeRating> ageRatings,
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        List<Performance> performances = performanceService.getFilteredAndSorted(
                title, genre, minDuration, startDate, ageRatings, sortBy, direction);

        model.addAttribute("performances", performances);
        model.addAttribute("currentFilters", new CurrentFilters(
                title, genre, minDuration, startDate, sortBy, direction, ageRatings
        ));
        model.addAttribute("selectedAgeRatings", ageRatings != null ? ageRatings : List.of());
        model.addAttribute("allGenres", Genre.values());

        return "home";
    }

    public record CurrentFilters(
            String title,
            Genre genre,
            int minDuration,
            LocalDate startDate,
            String sortBy,
            String direction,
            List<AgeRating> ageRatings
    ) {}
}

