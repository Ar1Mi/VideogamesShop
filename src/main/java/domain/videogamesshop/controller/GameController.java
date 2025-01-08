package domain.videogamesshop.controller;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}