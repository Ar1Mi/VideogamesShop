package domain.videogamesshop.service;

import domain.videogamesshop.model.Game;
import domain.videogamesshop.model.Review;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findByGame(Game game) {
//        return game.getReviews();
        return reviewRepository.findByGame(game);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> findByGameAndUser(Game game, User user) {
        return reviewRepository.findByGameAndUser(game, user);
    }
}
