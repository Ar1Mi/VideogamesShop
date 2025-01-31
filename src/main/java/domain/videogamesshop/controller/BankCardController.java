package domain.videogamesshop.controller;

import domain.videogamesshop.model.BankCard;
import domain.videogamesshop.model.User;
import domain.videogamesshop.service.BankCardService;
import domain.videogamesshop.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/bankcard")
public class BankCardController {

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bankCard", new BankCard());
        return "bankcard_new"; // шаблон bankcard_new.html
    }

    @PostMapping("/new")
    public String createBankCard(@Valid @ModelAttribute("bankCard") BankCard bankCard,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 Model model) {
        // Проверяем валидацию полей (номер карты и CVV)
        if (bindingResult.hasErrors()) {
            return "bankcard_new";
        }

        // Проверяем, что пользователь залогинен
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Привязываем карту к пользователю
        bankCard.setUser(user);

        // Сохраняем
        bankCardService.save(bankCard);

        // Перенаправляем в корзину, например
        return "redirect:/cart";
    }

    @GetMapping("/delete/{id}")
    public String deleteBankCard(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            bankCardService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Bank card deleted successfully.");
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

}