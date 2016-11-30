import com.google.gson.Gson;
import objects.User;
import proto.AuthData;
import storage.SessionStorage;
import storage.UsersStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/auth/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Gson gson = new Gson();
        AuthData authData = gson.fromJson(postBody, AuthData.class);

        UsersStorage usersStorage = new UsersStorage();
        SessionStorage sessionStorage = new SessionStorage();
        User user;

        try {
            user = usersStorage.getUserByLogin(authData.login);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if(user != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, String.format("User \"%s\" already registered", authData.login));
            return;
        }

        try {
            usersStorage.newUser(authData.login, authData.password);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}