import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.Session;
import objects.Task;
import objects.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

public class SharedServletTest {
    @Test
    public void doGet() throws Exception {
        String login = UUID.randomUUID().toString();
        String passwd = UUID.randomUUID().toString();

        TasksStorage tasksStorage = new TasksStorage();
        UsersStorage usersStorage = new UsersStorage();
        User user = usersStorage.newUser(login, passwd);

        tasksStorage.newTask(user.getId(), "task", "description", false, true);

        Gson gson = new Gson();
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/todo/shared");

        HttpResponse resp = httpclient.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        String json = reader.lines().collect(Collectors.joining());

        assertEquals(200, resp.getStatusLine().getStatusCode());

        List<Task> tasks = gson.fromJson(json, new TypeToken<List<Task>>() {}.getType());

        assertTrue(tasks.stream().allMatch(Task::isShared));
        assertTrue(tasks.stream()
                .filter(task -> task.getOwnerId().equals(user.getId()))
                .findFirst()
                .isPresent());
    }
}