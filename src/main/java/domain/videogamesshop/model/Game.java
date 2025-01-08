package domain.videogamesshop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // Название
    private String developer;    // Разработчик
    private String genre;        // Жанр
    private int year;            // Год выпуска
    private double fileSize;     // Вес файла (можно уточнить единицы - МБ, ГБ и т.д.)
    private String os;           // Поддерживаемая ОС
}
