package objects;

import java.util.UUID;

public class User {
    private String id;
    private String login;
    private String password;

    public User(String login, String password) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
    }

    public User(String id, String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
