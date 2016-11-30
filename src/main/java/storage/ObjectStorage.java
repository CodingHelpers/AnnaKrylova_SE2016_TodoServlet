package storage;

import com.google.gson.Gson;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectStorage<T> {
    protected Path filePath;
    protected Type objectType;
    protected List<T> objects = new ArrayList<>();
    private static Mutex mutex = new Mutex();


    protected ObjectStorage(Type objectType) {
        this.objectType = objectType;
        filePath = Paths.get(configDirPath().toString(), objectType.getTypeName());
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException, InterruptedException {
        mutex.acquire();

        objects.clear();
        Gson gson = new Gson();

        if(Files.exists(filePath)) {
            BufferedReader reader = Files.newBufferedReader(filePath);
            reader.lines()
                    .map((line) -> gson.fromJson(line, objectType))
                    .forEach((obj) -> objects.add((T) obj));
            reader.close();
        }

        mutex.release();
    }

    public void save() throws IOException, InterruptedException {
        mutex.acquire();

        Gson gson = new Gson();

        if(!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        BufferedWriter writer = Files.newBufferedWriter(filePath);
        for(String json: objects.stream()
                                .map((obj) -> gson.toJson(obj, objectType))
                                .collect(Collectors.toList()))
        {
            writer.write(json);
            writer.write("\n");
        }
        writer.close();

        mutex.release();
    }

    private static Path configDirPath() {
        String homeDir = System.getenv("HOME");
        Path confDirPath = Paths.get(homeDir, "/.config/todo/");
        if(!Files.isDirectory(confDirPath)) {
            try {
                Files.createDirectories(confDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return confDirPath;
    }
}


