package domain.videogamesshop.controller;

import domain.videogamesshop.model.*;
import domain.videogamesshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CustomUserDetailsService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/addGame/{gameId}")
    public String addGameToCart(@PathVariable Long gameId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        cartService.addGameToUserCart(user, game);
        return "redirect:/games";
    }

    // Показать корзину
    @GetMapping
    public String showCart(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<BankCard> cards = bankCardService.findByUser(user);
        Cart cart = cartService.getCartByUser(user);

        model.addAttribute("cart", cart);
        model.addAttribute("cards", cards);

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }
        return "cart"; // cart.html
    }

    @GetMapping("/removeGame/{gameId}")
    public String removeGameFromCartt(@PathVariable Long gameId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Cart cart = cartService.getCartByUser(user);
        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра с ID " + gameId + " не найдена"));

        if (!cart.getGames().contains(game)) {
            redirectAttributes.addFlashAttribute("error", "Игра не найдена в вашей корзине.");
            return "redirect:/cart";
        }

        cart.getGames().remove(game);

        double newTotal = cart.getGames().stream()
                .mapToDouble(Game::getPrice)
                .sum();
        cart.setTotalPrice(newTotal);

        cartService.save(cart);

        redirectAttributes.addFlashAttribute("success", "Игра успешно удалена из корзины.");
        return "redirect:/cart";
    }


    @PostMapping("/checkout")
    public String doCheckout(@RequestParam("cardId") Long cardId,
                             Principal principal,
                             Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByLogin(principal.getName()).orElseThrow();
        Cart cart = cartService.getCartByUser(user);

        BankCard card = bankCardService.findById(cardId).orElseThrow();

        double total = cart.getTotalPrice();
        if (total > card.getBalance()) {

            model.addAttribute("error", "Недостаточно средств на карте!");
            model.addAttribute("cart", cart);
            model.addAttribute("cards", bankCardService.findByUser(user));
            return "cart"; // Возвращаемся на страницу корзины с сообщением об ошибке
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(total);
        transaction.setUser(user);
        transaction.setBankCard(card);
        transaction.setGames(new HashSet<>(cart.getGames()));
        transactionService.save(transaction);

        card.setBalance(card.getBalance() - total);
        bankCardService.save(card);

        cart.getGames().clear();
        cart.setTotalPrice(0.0);
        cartService.save(cart);

        return "redirect:/games";
    }

    @PostMapping("/addGame/{gameId}")
    public String addGameToCart(@PathVariable Long gameId,
                                Principal principal,
                                RedirectAttributes redir) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByLogin(principal.getName()).orElseThrow();
        Game game = gameService.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));
        cartService.addGameToUserCart(user, game);
        // Можно добавить flash-сообщение
        // redir.addFlashAttribute("success", "Игра добавлена в корзину");
        // Возвращаем назад на /games, чтобы обновилось отображение
        return "redirect:/games";
    }

    @PostMapping("/removeGame/{gameId}")
    public String removeGameFromCart(@PathVariable Long gameId,
                                     Principal principal,
                                     RedirectAttributes redir) {
        removeGameFromCartt(gameId, principal, redir);
        return "redirect:/games";
    }

}
