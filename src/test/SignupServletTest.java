import com.google.gson.Gson;
import objects.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import proto.AuthData;
import storage.UsersStorage;

import java.util.UUID;

import static org.junit.Assert.*;

public class SignupServletTest {
    @Test
    public void doPost() throws Exception {
        UsersStorage usersStorage = new UsersStorage();
        usersStorage.getUsers();

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://localhost:8080/todo/auth/signup");

        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        AuthData authData = new AuthData(login, passwd);

        httppost.setEntity(new StringEntity(gson.toJson(authData, AuthData.class), ContentType.APPLICATION_JSON));

        HttpResponse resp = httpclient.execute(httppost);
        assertEquals(200, resp.getStatusLine().getStatusCode());

        User registeredUser = usersStorage.getUserByLogin(login);
        assertNotEquals(null, registeredUser);
        assertEquals(registeredUser.getLogin(), login);
        assertEquals(registeredUser.getPassword(), passwd);
    }
}