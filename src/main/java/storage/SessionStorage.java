package storage;

import objects.Session;

import java.io.IOException;
import java.util.Optional;

public class SessionStorage extends ObjectStorage<Session> {
    public SessionStorage() {
        super(Session.class);
    }

    public Session newSession(String userID) throws IOException, InterruptedException {
        load();

        Session session = new Session(userID);
        this.objects.add(session);

        save();

        return session;
    }

    public Session getSession(String token) throws IOException, InterruptedException {
        load();

        Optional<Session> opt_session = this.objects.stream()
                .filter((s) -> s.getToken().equals(token))
                .findFirst();

        if(opt_session.isPresent()) {
            return opt_session.get();
        }

        return null;
    }
}
