package domain.videogamesshop.service;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }

    public List<Game> searchAndSortGames(String keyword, String sortField, String direction) {
        List<Game> allGames = gameRepository.findAll();

        if(keyword != null && !keyword.isBlank()) {
            allGames = allGames.stream()
                    .filter(game -> matchGame(game, keyword))
                    .toList();
        }
        if("year".equalsIgnoreCase(sortField)) {
            if ("desc".equalsIgnoreCase(direction)){
                allGames = allGames.stream()
                        .sorted(Comparator.comparingInt(Game::getYear).reversed())
                        .toList();
            } else { //asc
                allGames = allGames.stream()
                        .sorted(Comparator.comparingInt(Game::getYear))
                        .toList();
            }
        }
        return allGames;
    }

    private boolean matchGame(Game game, String keyword) {
        String lower = keyword.toLowerCase();

        // name или developer
        if (game.getName().toLowerCase().contains(lower)
                || game.getDeveloper().toLowerCase().contains(lower)) {
            return true;
        }

        // жанры
        if (game.getGenres() != null) {
            boolean found = game.getGenres().stream()
                    .anyMatch(g -> g.getName().toLowerCase().contains(lower));
            if (found) return true;
        }

        // платформы
        if (game.getPlatforms() != null) {
            boolean found = game.getPlatforms().stream()
                    .anyMatch(p -> p.getName().toLowerCase().contains(lower));
            if (found) return true;
        }

        return false;
    }
}