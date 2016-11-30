package objects;

import java.util.UUID;

public class Session {
    private String userId;
    private String token;

    public Session(String userId) {
        this.userId = userId;
        this.token = UUID.randomUUID().toString();
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
