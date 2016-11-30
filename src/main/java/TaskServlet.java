import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.Session;
import objects.Task;
import objects.User;
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
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@WebServlet("/task/*")
public class TaskServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

            if(user.getId().equals(task.getOwnerId())) {
                Task taskToChange = tasksStorage.getTasksByID(task.getId());
                if (taskToChange == null) {
                    resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No task with id " + task.getId());
                    return;
                }

                taskToChange.setName(task.getName());
                taskToChange.setDescription(task.getDescription());
                taskToChange.setDone(task.isDone());
                taskToChange.setShared(task.isShared());

                tasksStorage.save();
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only owner can modify tasks");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        resp.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = Helpers.getTokenCookie(req);

        if(token == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized");
            return;
        }

        String taskID = req.getRequestURI();
        taskID = taskID.substring(taskID.indexOf("task/") + 5);

        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        TasksStorage tasksStorage = new TasksStorage();

        try {
            Session session = sessionStorage.getSession(token);
            if(session == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            }

            User user = usersStorage.getUserByID(session.getUserId());
            Task task = tasksStorage.getTasksByID(taskID);

            if(task == null) {
                resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No task with id " + taskID);
                return;
            }

            if(user.getId().equals(task.getOwnerId())) {
                tasksStorage.deleteTask(task.getId());
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only owner can delete tasks");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        resp.setStatus(200);
    }

}

