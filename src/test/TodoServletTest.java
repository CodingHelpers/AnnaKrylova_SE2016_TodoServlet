import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.Session;
import objects.Task;
import objects.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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


public class TodoServletTest {
    @Test
    public void doGet() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        TasksStorage tasksStorage = new TasksStorage();
        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);
        Session session = sessionStorage.newSession(user.getId());

        for(int i = 0; i < 10; i++) {
            tasksStorage.newTask(user.getId(), "task" + i, "description" + i, false, false);
        }

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/todo/");
        httpGet.addHeader("Cookie", "token="+session.getToken());

        HttpResponse resp = httpclient.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        String json = reader.lines().collect(Collectors.joining());

        assertEquals(200, resp.getStatusLine().getStatusCode());

        List<Task> tasks = gson.fromJson(json, new TypeToken<List<Task>>() {}.getType());
        assertEquals(10, tasks.size());
        for(int i = 0; i < 10; i++) {
            Task task = tasks.get(i);
            assertEquals(task.getName(), "task" + i);
            assertEquals(task.getDescription(), "description" + i);
            assertEquals(task.getOwnerId(), user.getId());
        }

    }

    @Test
    public void doPut() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        TasksStorage tasksStorage = new TasksStorage();
        SessionStorage sessionStorage = new SessionStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);
        Session session = sessionStorage.newSession(user.getId());

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut("http://localhost:8080/todo/");
        httpPut.addHeader("Cookie", "token="+session.getToken());

        Task task = new Task(user.getId(), "task", "description", false, true);
        httpPut.setEntity(new StringEntity(gson.toJson(task, Task.class), ContentType.APPLICATION_JSON));

        HttpResponse resp = httpclient.execute(httpPut);
        assertEquals(200, resp.getStatusLine().getStatusCode());

        List<Task> tasks = tasksStorage.getTasksOfUser(user.getId());
        assertEquals(1, tasks.size());
        assertEquals(task.getName(), tasks.get(0).getName());
        assertEquals(task.getDescription(), tasks.get(0).getDescription());
        assertEquals(task.getOwnerId(), tasks.get(0).getOwnerId());
        assertEquals(task.isDone(), tasks.get(0).isDone());
        assertEquals(task.isShared(), tasks.get(0).isShared());
    }

}