package objects;

import java.util.UUID;

public class Task {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private boolean done;
    private boolean shared;

    public Task(String id, String ownerId, String name, String description, boolean done, boolean shared) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.done = done;
        this.shared = shared;
    }

    public Task(String ownerId, String name, String description, boolean done, boolean shared) {
        this.id = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.done = done;
        this.shared = shared;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (done != task.done) return false;
        if (shared != task.shared) return false;
        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        if (ownerId != null ? !ownerId.equals(task.ownerId) : task.ownerId != null) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        return description != null ? description.equals(task.description) : task.description == null;
    }
}
