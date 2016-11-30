import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.Session;
import objects.Task;
import objects.User;
import org.apache.commons.codec.Charsets;
import storage.SessionStorage;
import storage.TasksStorage;
import storage.UsersStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@WebServlet("/")
public class TodoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int cnt = Integer.MAX_VALUE;
        int offset = 0;

        String cntParameter = req.getParameter("cnt");
        String ofstParameter = req.getParameter("offset");

        if(cntParameter != null) {
            cnt = Integer.parseInt(cntParameter);
            if(cnt < 0) {
                cnt = 0;
            }
        }

        if(ofstParameter != null) {
            offset = Integer.parseInt(ofstParameter);
            if(offset < 0) {
                offset = 0;
            }
        }

        String token = Helpers.getTokenCookie(req);

        if(token == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized");
            return;
        }

        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        TasksStorage tasksStorage = new TasksStorage();
        Gson gson = new Gson();

        try {
            Session session = sessionStorage.getSession(token);
            if(session == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            }

            User user = usersStorage.getUserByID(session.getUserId());
            List<Task> tasks = tasksStorage.getTasksOfUser(user.getId())
                    .stream()
                    .skip(offset)
                    .limit(cnt)
                    .collect(Collectors.toList());

            Type typeToken = new TypeToken<List<Task>>() {}.getType();
            String json = gson.toJson(tasks, typeToken);
            json = new String(json.getBytes(Charsets.UTF_8));

            resp.setContentType("application/json; charset='UTF-8'");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), "UTF8"), true);
            out.write(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        resp.setStatus(200);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = Helpers.getTokenCookie(req);

        if(token == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized");
            return;
        }

        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        TasksStorage tasksStorage = new TasksStorage();
        Gson gson = new Gson();

        try {
            Session session = sessionStorage.getSession(token);
            if(session == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            }

            User user = usersStorage.getUserByID(session.getUserId());
            String json = req.getReader().lines().collect(Collectors.joining());
            Task task = gson.fromJson(json, Task.class);

            tasksStorage.newTask(user.getId(), task.getName(), task.getDescription(), task.isDone(), task.isShared());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        resp.setStatus(200);
    }
}

