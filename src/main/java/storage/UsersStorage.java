package storage;

import objects.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UsersStorage extends ObjectStorage<User> {
    public UsersStorage() {
        super(User.class);
    }

    public List<User> getUsers() throws IOException, InterruptedException {
        load();
        return this.objects;
    }

    public User getUserByLogin(String login) throws IOException, InterruptedException {
        Optional<User> optUser = getUsers().stream()
                .filter((user) -> user.getLogin().equals(login))
                .findFirst();

        if(optUser.isPresent()) {
            return optUser.get();
        }

        return null;
    }

    public User getUserByID(String login) throws IOException, InterruptedException {
        Optional<User> optUser = getUsers().stream()
                .filter((user) -> user.getId().equals(login))
                .findFirst();

        if(optUser.isPresent()) {
            return optUser.get();
        }

        return null;
    }

    public User newUser(String login, String password) throws IOException, InterruptedException {
        load();

        User user = new User(login, password);
        this.objects.add(user);

        save();
        return user;
    }
}
