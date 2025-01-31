package domain.videogamesshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "{review.title.notBlank}")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "{review.content.notBlank}")
    private String content;

    @Column(nullable = false)
    @Min(value = 0, message = "{review.rating.min}")
    @Max(value = 5, message = "{review.rating.max}")
    private short rating; // 0-5

    @Column(nullable = false)
    private LocalDateTime date; // Дата отзыва

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public Review(String title, String content, short rating, LocalDateTime date, User user, Game game) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.date = date;
        this.user = user;
        this.game = game;
    }

    public Review() {
    }

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", date=" + date +
                ", user=" + user +
                ", game=" + game +
                '}';
    }
}
