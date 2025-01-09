package domain.videogamesshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "games")
@Getter
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{game.name.notBlank}")
    private String name;         // Название

    @NotBlank(message = "{game.developer.notBlank}")
    private String developer;    // Разработчик

    @NotBlank(message = "{game.genre.notBlank}")
    private String genre;        // Жанр

    @Min(value = 1970, message = "{game.year.min}")
    @Max(value = 2050, message = "{game.year.max}")
    private int year;            // Год выпуска

    @PositiveOrZero(message = "{game.fileSize.positiveOrZero}")
    private double fileSize;     // Вес файла

    @NotBlank(message = "{game.os.notBlank}")
    private String os;           // Поддерживаемая ОС

    // Поле для ссылки на картинку
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
