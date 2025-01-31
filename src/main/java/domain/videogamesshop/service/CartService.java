package domain.videogamesshop.service;

import domain.videogamesshop.model.Cart;
import domain.videogamesshop.model.Game;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.CartRepository;
import domain.videogamesshop.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private GameRepository gameRepository;

    // Найти корзину пользователя, если нет — создать пустую
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    // Создаём новую корзину, если её нет
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });
    }

    // Добавляем игру в корзину
    public void addGameToUserCart(User user, Game game) {
        Cart cart = getCartByUser(user);
        // Добавляем игру в сет, если её ещё нет
        cart.getGames().add(game);

        // При необходимости пересчитываем totalPrice
        // (если у Game есть поле "price", а у вас — логика подсчёта)
         double newTotal = cart.getGames().stream().mapToDouble(Game::getPrice).sum();
         cart.setTotalPrice(newTotal);

        cartRepository.save(cart);
    }

    // Удалить игру из корзины (если понадобится)
    public void removeGameFromUserCart(User user, Game game) {
        Cart cart = getCartByUser(user);
        cart.getGames().remove(game);

        if (cart.getGames().contains(game)) {
            cart.getGames().remove(game);
            double newTotal = cart.getGames().stream()
                    .mapToDouble(Game::getPrice)
                    .sum();
            cart.setTotalPrice(newTotal);
            cartRepository.save(cart);
        }
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
