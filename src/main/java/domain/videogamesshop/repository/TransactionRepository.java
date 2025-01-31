package domain.videogamesshop.repository;

import domain.videogamesshop.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // при необходимости методы поиска

    boolean existsByBankCardId(Long bankCardId);
}