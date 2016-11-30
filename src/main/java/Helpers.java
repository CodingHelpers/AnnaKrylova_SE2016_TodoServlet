import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Helpers {
    public static String getTokenCookie(HttpServletRequest req) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();

        // Find token
        String token = req.getHeader("Token");
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }


        return token;
    }
}
