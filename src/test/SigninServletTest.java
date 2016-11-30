import com.google.gson.Gson;
import objects.Session;
import objects.User;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import proto.AuthData;
import storage.SessionStorage;
import storage.UsersStorage;

import java.util.UUID;

import static org.junit.Assert.*;

public class SigninServletTest {
    @Test
    public void doPost() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://localhost:8080/todo/auth/signin");

        AuthData authData = new AuthData(login, passwd);

        httppost.setEntity(new StringEntity(gson.toJson(authData, AuthData.class), ContentType.APPLICATION_JSON));

        HttpResponse resp = httpclient.execute(httppost);
        assertEquals(200, resp.getStatusLine().getStatusCode());
        String tokenCookie = resp.getFirstHeader("Set-Cookie").getValue();
        String token = tokenCookie.substring(tokenCookie.indexOf("=") + 1);

        Session session = sessionStorage.getSession(token);

        assertNotEquals(null, session);
        assertEquals(session.getUserId(), user.getId());
    }
}