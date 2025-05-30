package ru.theater.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.theater.info.model.User;
import ru.theater.info.repository.UserRepository;


@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            Model model
    ) {
        if (error != null) model.addAttribute("error", "Неверные учетные данные");
        if (registered != null) model.addAttribute("success", "Регистрация успешна!");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(User user, RedirectAttributes redirectAttributes) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                                                "Имя пользователя уже занято");
            return "redirect:/register";
        }

        if (user.getMail() != null && !user.getMail().isEmpty()) {
            if (!user.getMail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                redirectAttributes.addFlashAttribute("error", "Некорректный формат email");
                return "redirect:/register";
            }
            if (userRepo.findByMail(user.getMail()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email уже зарегистрирован");
                return "redirect:/register";
            }
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            if (!user.getPhone().matches("^(\\+7|8)\\d{10}$")) {
                redirectAttributes.addFlashAttribute("error", "Телефон должен быть в формате +79161234567 или 89161234567");
                return "redirect:/register";
            }
            if (userRepo.findByPhone(user.getPhone()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Телефон уже зарегистрирован");
                return "redirect:/register";
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepo.save(user);
        return "redirect:/login?registered";
    }

}
