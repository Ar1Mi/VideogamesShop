package domain.videogamesshop.controller;

import domain.videogamesshop.model.Role;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.RoleRepository;
import domain.videogamesshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()){
            return "register";
        }

        Role role = roleRepository.findByName("VISITOR")
                .orElseThrow(() -> new RuntimeException("Role VISITOR not foud"));
        user.setRole(role);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
