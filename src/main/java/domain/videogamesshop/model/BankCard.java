package domain.videogamesshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Random;

@Entity
@Table(name = "bank_cards")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Номер карты (обычно 16 цифр), тут хранится как строка
    @Column(nullable = false, length = 16)
    @NotBlank(message = "{bankcard.cardNumber.notBlank}")
    @Size(min = 16, max = 16, message = "{bankcard.cardNumber.size}")
    @Pattern(regexp = "\\d{16}", message = "{bankcard.cardNumber.pattern}")
    private String cardNumber;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "{bankcard.cvv.notBlank}")
    @Size(min = 3, max = 3, message = "{bankcard.cvv.size}")
    @Pattern(regexp = "\\d{3}", message = "{bankcard.cvv.pattern}")
    private String cvv;

    private double balance;

    // Владелец карты (один пользователь может иметь несколько карт)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public BankCard() {
        Random random = new Random();
        balance = random.nextInt(401);
    }
}
