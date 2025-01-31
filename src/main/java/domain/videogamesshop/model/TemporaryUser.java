package domain.videogamesshop.model;

public class TemporaryUser {
    private final User user;
    private final String verificationCode;

    public TemporaryUser(User user, String verificationCode) {
        this.user = user;
        this.verificationCode = verificationCode;
    }

    public User getUser() {
        return user;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
