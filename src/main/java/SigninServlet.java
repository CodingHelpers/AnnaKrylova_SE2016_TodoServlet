import com.google.gson.Gson;
import objects.Session;
import objects.User;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;
import proto.AuthData;
import storage.SessionStorage;
import storage.UsersStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/auth/signin")
public class SigninServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String postBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(postBody);
        Gson gson = new Gson();
        AuthData authData = gson.fromJson(postBody, AuthData.class);

        UsersStorage usersStorage = new UsersStorage();
        SessionStorage sessionStorage = new SessionStorage();

        try {
            User user = usersStorage.getUserByLogin(authData.login);

            if(user == null || !user.getPassword().equals(authData.password)) {
                resp.getWriter().write("Wrong login or password");
                return;
            }

            Session session = sessionStorage.newSession(user.getId());
            Cookie tokenCookie = new Cookie("token", session.getToken());
            tokenCookie.setDomain("localhost");
            tokenCookie.setPath("/");
            resp.addCookie(tokenCookie);

            resp.getWriter().write(session.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }


        resp.setStatus(HttpServletResponse.SC_OK);
    }

}