package domain.videogamesshop.controller;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.model.Platform;
import domain.videogamesshop.service.FileService;
import domain.videogamesshop.service.GameService;
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
        model.addAttribute("platforms", platformService.findAll());
        return "edit";
    }

    @PostMapping("/games/edit")
    public String updateGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             @RequestParam(value="platformIds", required = false) List<Long> platformIds,
                             @RequestParam("file") MultipartFile file, // получаем загруженный файл
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }

        // Находим старый объект (чтобы узнать старый файл, если было изображение)
        Game oldGame = gameService.findById(game.getId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Если пользователь загрузил новый файл, удаляем старый и сохраняем новый
        if (file != null && !file.isEmpty()) {
            // Удаляем старый файл (если был)
            if (oldGame.getImageUrl() != null && !oldGame.getImageUrl().isBlank()) {
                fileService.deleteFile(oldGame.getImageUrl());
            }
            // Сохраняем новый
            String newFilename = fileService.saveFile(file);
            game.setImageUrl(newFilename); // в БД мы храним имя файла, например "uuid_originalName.jpg"
        } else {
            // Если новый файл не загружен, оставляем старый путь к картинке
            game.setImageUrl(oldGame.getImageUrl());
        }

        Set<Platform> selectedPlatforms = new HashSet<>();
        if (platformIds != null) {
            for (Long platformId : platformIds) {
                Platform p = platformService.findById(platformId)
                        .orElseThrow(() -> new RuntimeException("Platform not found"));
                selectedPlatforms.add(p);
            }
        }
        game.setPlatforms(selectedPlatforms);

        // Сохраняем обновлённую игру
        gameService.save(game);

        return "redirect:/";
    }

    @GetMapping("/games/new")
    public String showCreateForm(Model model) {
        // Создаём пустой объект Game для привязки формы
        Game newGame = new Game();
        model.addAttribute("game", newGame);
        model.addAttribute("platforms", platformService.findAll());
        return "new"; // возврат к шаблону new.html
    }

    @PostMapping("/games/new")
    public String createGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             @RequestParam(value="platformIds", required = false) List<Long> platformIds,
                             @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, просто возвращаем ту же форму
            return "new";
        }

        // Если пользователь загрузил файл
        if (file != null && !file.isEmpty()) {
            String filename = fileService.saveFile(file);
            game.setImageUrl(filename);
        }

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