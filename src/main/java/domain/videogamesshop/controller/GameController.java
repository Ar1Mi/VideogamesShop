package domain.videogamesshop.controller;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/")
    public String showGamesList(Model model) {
        List<Game> games = gameService.findAllGames();
        model.addAttribute("games", games);
        return "index";  // возвращаем имя шаблона (index.html)
    }

    @GetMapping("/games/details/{id}")
    public String showGameDetails(@PathVariable Long id, Model model) {
        // Получаем игру по ID (можно использовать метод findById из JPA)
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        // Передаём объект game в модель
        model.addAttribute("game", game);
        // Возвращаем имя Thymeleaf-шаблона, например "details"
        return "details";
    }

    //Отобразить форму редактирования
    @GetMapping("/games/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        model.addAttribute("game", game);
        return "edit";  // edit.html
    }

    // Сохранить изменения (POST)
    @PostMapping("/games/edit")
    public String updateGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             Model model) {
        // Проверяем, есть ли ошибки валидации
        if (bindingResult.hasErrors()) {
            // Возвращаемся обратно на форму редактирования,
            // где отобразим сообщения об ошибках
            return "edit";
        }

        // Если ошибок нет — сохраняем (обновляем) данные
        gameService.save(game);

        // Редирект на главную страницу (или куда нужно)
        return "redirect:/";
    }
}