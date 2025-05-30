package ru.theater.info.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.theater.info.*;
import ru.theater.info.enumModel.AgeRating;
import ru.theater.info.enumModel.Genre;
import ru.theater.info.model.Performance;
import ru.theater.info.model.User;
import ru.theater.info.service.PerformanceService;
import ru.theater.info.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final PerformanceService performanceService;

    public AdminController(UserService userService,
                           PerformanceService performanceService) {
        this.userService = userService;
        this.performanceService = performanceService;
    }

    // ==================== Управление постановками ====================

    @GetMapping("/performances")
    public String showPerformances(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Genre genre,
            @RequestParam(defaultValue = "0") int minDuration,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) List<AgeRating> ageRatings, // Новый параметр
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        List<Performance> performances = performanceService.getFilteredAndSorted(
                title, genre, minDuration, startDate, ageRatings, sortBy, direction);

        model.addAttribute("performances", performances);
        model.addAttribute("currentFilters", new HomeController.CurrentFilters(
                title, genre, minDuration, startDate, sortBy, direction, ageRatings
        ));
        model.addAttribute("selectedAgeRatings", ageRatings != null ? ageRatings : List.of());
        model.addAttribute("allGenres", Genre.values());


        return "admin/performances";
    }

    @GetMapping("/performances/new")
    public String showAddPerformanceForm(Model model) {
        model.addAttribute("performance", new Performance());
        model.addAttribute("selectedAgeRatings", List.of());
        model.addAttribute("allGenres", Genre.values());
        return "admin/performance-form";
    }

    @PostMapping("/performances")
    public String addPerformance(@ModelAttribute Performance performance) {
        performanceService.savePerformance(performance);
        return "redirect:/admin/performances";
    }

    @GetMapping("/performances/edit/{id}")
    public String showEditPerformanceForm(@PathVariable Long id, Model model) {
        Performance performance = performanceService.getPerformanceById(id);
        model.addAttribute("performance", performance);
        model.addAttribute("selectedAgeRatings", List.of());
        model.addAttribute("allGenres", Genre.values());
        return "admin/performance-form";
    }

    @PostMapping("/performances/{id}")
    public String updatePerformance(@PathVariable Long id,
                                    @ModelAttribute Performance performance) {
        performanceService.updatePerformance(id, performance);
        return "redirect:/admin/performances";
    }

    @PostMapping("/performances/delete/{id}")
    public String deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return "redirect:/admin/performances";
    }

    // ==================== Управление пользователями ====================

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/toggle-admin")
    public String toggleAdminRole(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.getUserByUsername(authentication.getName());
            userService.toggleAdminRole(userId, currentUser.getId());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.getUserByUsername(authentication.getName());
            userService.deleteUser(id, currentUser.getId());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }


}

