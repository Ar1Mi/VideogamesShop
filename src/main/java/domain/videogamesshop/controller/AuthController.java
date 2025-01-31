package domain.videogamesshop.controller;

import domain.videogamesshop.model.Role;
import domain.videogamesshop.model.TemporaryUser;
import domain.videogamesshop.model.TemporaryUserStorage;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.RoleRepository;
import domain.videogamesshop.repository.UserRepository;
import domain.videogamesshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user,
                                  Model model) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()){
            return "register";
        }

        Role role = roleRepository.findByName("VISITOR")
                .orElseThrow(() -> new RuntimeException("Role VISITOR not foud"));
        user.setRole(role);

        String verificationCode = generateVerificationCode();
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        // Сохраняем пользователя во временной структуре
        TemporaryUserStorage.saveTemporaryUser(user, verificationCode);
        model.addAttribute("email", user.getEmail());
        return "redirect:/verify-email?email=" + user.getEmail();
    }

    @GetMapping("/verify-email")
    public String verifyEmailPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("email") String email,
                              @RequestParam("code") String code, Model model) {
        TemporaryUser temporaryUser = TemporaryUserStorage.getTemporaryUser(email);

        if (temporaryUser == null || !temporaryUser.getVerificationCode().equals(code)) {
            model.addAttribute("error", "Неверный код подтверждения");
            model.addAttribute("email", email);
            return "verify-email";
        }

        User user = temporaryUser.getUser();
        Role role = roleRepository.findByName("VISITOR")
                .orElseThrow(() -> new RuntimeException("Role VISITOR not found"));
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Удаляем временную запись
        TemporaryUserStorage.removeTemporaryUser(email);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Генерация 6-значного кода
    }
}
