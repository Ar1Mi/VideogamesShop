package domain.videogamesshop.repository;

import domain.videogamesshop.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // При необходимости добавить кастомные запросы (JPQL или @Query)
}