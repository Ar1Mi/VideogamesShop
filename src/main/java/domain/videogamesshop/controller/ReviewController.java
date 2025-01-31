package domain.videogamesshop.controller;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.model.Review;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.ReviewRepository;
import domain.videogamesshop.service.CustomUserDetailsService;
import domain.videogamesshop.service.GameService;
import domain.videogamesshop.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private GameService gameService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @PostMapping("/reviews/add")
    public String addReview(@ModelAttribute("review") Review review,
                            Principal principal) {
        // principal содержит логин аутентифицированного пользователя
        if (principal == null) {
            // Если пользователь не залогинен, можно вернуть ошибку или редирект
            return "redirect:/login";
        }

        // Получаем объект User из БД по логину
        User user = customUserDetailsService.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Устанавливаем автора отзыва (текущего пользователя)
        review.setUser(user);

        // У review уже установлен game.id из формы
        // но game может быть не загружен полностью,
        // поэтому иногда нужно подгрузить полную сущность:
        Game game = gameService.findById(review.getGame().getId())
                .orElseThrow(() -> new RuntimeException("Game not found"));
        review.setGame(game);

        // Сохраняем отзыв
        reviewService.save(review);

        // Перенаправляем обратно на страницу деталей игры
        return "redirect:/games/details/" + game.getId();
    }

}
