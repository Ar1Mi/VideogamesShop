package domain.videogamesshop.controller;

import domain.videogamesshop.model.*;
import domain.videogamesshop.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private CartService cartService;

//    @GetMapping("/games")
//    public String showGamesList(
//            @RequestParam(name = "keyword", required = false) String keyword,
//            @RequestParam(name = "sort", required = false, defaultValue = "none") String sortField,
//            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
//            Model model) {
//        List<Game> games = gameService.searchAndSortGames(keyword, sortField, direction);
//
//        model.addAttribute("games", games);
//        model.addAttribute("keywords", keyword);
//        model.addAttribute("direction", direction);
//        return "index";
//    }

    @GetMapping("/games")
    public String showGamesList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sort", required = false, defaultValue = "none") String sortField,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            Principal principal,
            Model model
    ) {
        List<Game> games = gameService.searchAndSortGames(keyword, sortField, direction);

        Set<Long> cartGameIds = new HashSet<>();
        if (principal != null) {
            User user = userService.findByLogin(principal.getName()).orElse(null);
            if (user != null) {
                Cart cart = cartService.getCartByUser(user);
                if (cart != null) {
                    cartGameIds = cart.getGames().stream()
                            .map(Game::getId)
                            .collect(Collectors.toSet());
                }
            }
        }

        model.addAttribute("games", games);
        model.addAttribute("keyword", keyword);
        model.addAttribute("direction", direction);
        model.addAttribute("cartGameIds", cartGameIds);

        return "index";
    }


    @GetMapping("/games/details/{id}")
    public String showGameDetails(@PathVariable Long id, Model model) {
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        List<Review> reviews = reviewService.findByGame(game);

        Review newReview = new Review();
        newReview.setGame(game);

        model.addAttribute("game", game);
        model.addAttribute("review", newReview);
        model.addAttribute("reviews", reviews);
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
                             @RequestParam("file") MultipartFile file,
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
            String newFilename = fileService.saveFile(file);
            game.setImageUrl(newFilename);
        } else {
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

        return "redirect:/games";
    }

    @GetMapping("/games/new")
    public String showCreateForm(Model model) {
        Game newGame = new Game();
        model.addAttribute("game", newGame);
        model.addAttribute("genres", genreService.findAllGenres());
        model.addAttribute("platforms", platformService.findAll());
        return "new";
    }

    @PostMapping("/games/new")
    public String createGame(@Valid @ModelAttribute("game") Game game,
                             BindingResult bindingResult,
                             @RequestParam(value="genreIds", required = false) List<Long> genreIds,
                             @RequestParam(value="platformIds", required = false) List<Long> platformIds,
                             @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "new";
        }

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

        gameService.save(game);

        return "redirect:/games";
    }

    @GetMapping("/games/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        Game game = gameService.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getImageUrl() != null && !game.getImageUrl().isBlank()) {
            fileService.deleteFile(game.getImageUrl());
        }

        gameService.deleteById(id);

        return "redirect:/games";
    }
}