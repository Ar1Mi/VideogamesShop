package domain.videogamesshop.repository;

import domain.videogamesshop.model.BankCard;
import domain.videogamesshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    // при необходимости методы поиска
    Optional<BankCard> findByCardNumber(String cardNumber);

    List<BankCard> findByUser(User user);
}
