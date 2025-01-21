package domain.videogamesshop.controller;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.model.Genre;
import domain.videogamesshop.model.Platform;
import domain.videogamesshop.service.FileService;
import domain.videogamesshop.service.GameService;
import domain.videogamesshop.service.GenreService;
import domain.videogamesshop.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private GenreService genreService;

    @GetMapping("/")
    public String showGamesList(Model model) {
        List<Game> games = gameService.findAllGames();
        model.addAttribute("games", games);
        return "index";
    }

    @GetMapping("/games/details/{id}")
    public String showGameDetails(@PathVariable Long id, Model model) {
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        model.addAttribute("game", game);
        return "details";
    }

    @GetMapping("/games/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        model.addAttribute("game", game);
        model.addAttribute("genres", genreService.findAllGenres());
        model.addAttribute("platforms", platformService.findAll());
        return "edit";
    }

    @PostMapping("/games/edit")
    public String updateGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             @RequestParam(value="genreIds", required = false) List<Long> genreIds,
                             @RequestParam(value="platformIds", required = false) List<Long> platformIds,
                             @RequestParam("file") MultipartFile file, // получаем загруженный файл
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }

        Game oldGame = gameService.findById(game.getId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (file != null && !file.isEmpty()) {
            if (oldGame.getImageUrl() != null && !oldGame.getImageUrl().isBlank()) {
                fileService.deleteFile(oldGame.getImageUrl());
            }
            // Сохраняем новый
            String newFilename = fileService.saveFile(file);
            game.setImageUrl(newFilename);
        } else {
            // Если новый файл не загружен, оставляем старый путь к картинке
            game.setImageUrl(oldGame.getImageUrl());
        }

        Set<Genre> selectedGenres = new HashSet<>();
        if(genreIds != null) {
            for (Long genreId : genreIds) {
                Genre genre = genreService.findGenreById(genreId)
                        .orElseThrow(() -> new RuntimeException("Genre not found"));
                selectedGenres.add(genre);
            }
        }
        game.setGenres(selectedGenres);

        Set<Platform> selectedPlatforms = new HashSet<>();
        if (platformIds != null) {
            for (Long platformId : platformIds) {
                Platform p = platformService.findById(platformId)
                        .orElseThrow(() -> new RuntimeException("Platform not found"));
                selectedPlatforms.add(p);
            }
        }
        game.setPlatforms(selectedPlatforms);

        gameService.save(game);

        return "redirect:/";
    }

    @GetMapping("/games/new")
    public String showCreateForm(Model model) {
        // Создаём пустой объект Game для привязки формы
        Game newGame = new Game();
        model.addAttribute("game", newGame);
        model.addAttribute("genres", genreService.findAllGenres());
        model.addAttribute("platforms", platformService.findAll());
        return "new"; // возврат к шаблону new.html
    }

    @PostMapping("/games/new")
    public String createGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             @RequestParam(value="genreIds", required = false) List<Long> genreIds,
                             @RequestParam(value="platformIds", required = false) List<Long> platformIds,
                             @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаем ту же форму
            return "new";
        }

        // Если пользователь загрузил файл
        if (file != null && !file.isEmpty()) {
            String filename = fileService.saveFile(file);
            game.setImageUrl(filename);
        }

        Set<Genre> selectedGenres = new HashSet<>();
        if(genreIds != null) {
            for (Long genreId : genreIds) {
                Genre genre = genreService.findGenreById(genreId)
                        .orElseThrow(() -> new RuntimeException("Genre not found"));
                selectedGenres.add(genre);
            }
        }
        game.setGenres(selectedGenres);

        Set<Platform> selectedPlatforms = new HashSet<>();
        if (platformIds != null) {
            for (Long platformId : platformIds){
                Platform p = platformService.findById(platformId)
                        .orElseThrow(() -> new RuntimeException("Platform not found"));
                selectedPlatforms.add(p);
            }
        }
        game.setPlatforms(selectedPlatforms);

        // Сохраняем в БД
        gameService.save(game);

        // Перенаправляем на главную, где список всех игр
        return "redirect:/";
    }

    @GetMapping("/games/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        // 1. Найти игру в БД (чтобы узнать, есть ли связанный файл)
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // 2. Если у игры есть загруженная картинка, удаляем её
        if (game.getImageUrl() != null && !game.getImageUrl().isBlank()) {
            fileService.deleteFile(game.getImageUrl());
        }

        // 3. Удаляем запись из БД
        gameService.deleteById(id);

        // 4. Редирект на главную страницу (список игр)
        return "redirect:/";
    }

}