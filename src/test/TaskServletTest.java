import com.google.gson.Gson;
import objects.Session;
import objects.Task;
import objects.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import storage.SessionStorage;
import storage.TasksStorage;
import storage.UsersStorage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TaskServletTest {
    @Test
    public void doPost() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        TasksStorage tasksStorage = new TasksStorage();
        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);
        Session session = sessionStorage.newSession(user.getId());

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/todo/task/");
        httpPost.addHeader("Cookie", "token="+session.getToken());

        Task task = tasksStorage.newTask(user.getId(), "task", "description", false, true);
        task.setName("new name");
        httpPost.setEntity(new StringEntity(gson.toJson(task, Task.class), ContentType.APPLICATION_JSON));

        HttpResponse resp = httpclient.execute(httpPost);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        String respBody = reader.lines().collect(Collectors.joining());

        assertEquals(200, resp.getStatusLine().getStatusCode());

        List<Task> tasks = tasksStorage.getTasksOfUser(user.getId());
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void doDelete() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        TasksStorage tasksStorage = new TasksStorage();
        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);
        Session session = sessionStorage.newSession(user.getId());

        Task task = tasksStorage.newTask(user.getId(), "task", "description", false, true);

        HttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete("http://localhost:8080/todo/task/"+task.getId());
        httpDelete.addHeader("Cookie", "token="+session.getToken());

        HttpResponse resp = httpclient.execute(httpDelete);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        String respBody = reader.lines().collect(Collectors.joining());

        assertEquals(200, resp.getStatusLine().getStatusCode());

        List<Task> tasks = tasksStorage.getTasksOfUser(user.getId());
        assertEquals(0, tasks.size());
    }
}