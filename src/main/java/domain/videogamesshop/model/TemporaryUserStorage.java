package domain.videogamesshop.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemporaryUserStorage {
    private static final Map<String, TemporaryUser> temporaryUsers = new ConcurrentHashMap<>();

    public static void saveTemporaryUser(User user, String verificationCode) {
        temporaryUsers.put(user.getEmail(), new TemporaryUser(user, verificationCode));
    }

    public static TemporaryUser getTemporaryUser(String email) {
        return temporaryUsers.get(email);
    }

    public static void removeTemporaryUser(String email) {
        temporaryUsers.remove(email);
    }
}
