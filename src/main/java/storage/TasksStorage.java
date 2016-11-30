package storage;

import objects.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TasksStorage extends ObjectStorage<Task> {
    public TasksStorage() {
        super(Task.class);
    }

    public List<Task> getTasks() throws IOException, InterruptedException {
        load();
        return this.objects;
    }

    public List<Task> getTasksOfUser(String userID) throws IOException, InterruptedException {
        return getTasks().stream()
                .filter((task) -> task.getOwnerId().equals(userID))
                .collect(Collectors.toList());
    }

    public List<Task> getSharedTasks() throws IOException, InterruptedException {
        return getTasks().stream()
                .filter(Task::isShared)
                .collect(Collectors.toList());
    }

    public Task getTasksByID(String ID) throws IOException, InterruptedException {
        Optional<Task> optTask = getTasks().stream()
                .filter((task) -> task.getId().equals(ID))
                .findFirst();

        if(optTask.isPresent()) {
            return optTask.get();
        }

        return null;
    }

    public void deleteTask(String id) throws IOException, InterruptedException {
        Task task = getTasksByID(id);
        if (task != null) {
            this.objects.remove(task);
        }
        save();
    }

    public Task newTask(String ownerId, String name, String description, boolean done, boolean shared) throws IOException, InterruptedException {
        load();

        Task task = new Task(ownerId, name, description, done, shared);
        this.objects.add(task);

        save();
        return task;
    }
}
