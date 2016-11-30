package storage;

import com.google.gson.Gson;
import org.junit.Test;
import storage.ObjectStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ObjectStorageTest {
    class TestData {
        int i;
        float f;
        String str;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestData testData = (TestData) o;

            if (i != testData.i) return false;
            if (Float.compare(testData.f, f) != 0) return false;
            return str != null ? str.equals(testData.str) : testData.str == null;

        }
    }

    @Test
    public void load() throws Exception {
        ObjectStorage<TestData> storage = new ObjectStorage<>(TestData.class);

        TestData testData = new TestData();
        testData.i = 5;
        testData.f = 999.999f;
        testData.str = "hello world";

        Gson gson = new Gson();
        BufferedWriter writer = Files.newBufferedWriter(storage.filePath);
        writer.write(gson.toJson(testData, TestData.class)); writer.write("\n");
        writer.write(gson.toJson(testData, TestData.class)); writer.write("\n");
        writer.write(gson.toJson(testData, TestData.class)); writer.write("\n");
        writer.write(gson.toJson(testData, TestData.class)); writer.write("\n");
        writer.write(gson.toJson(testData, TestData.class)); writer.write("\n");
        writer.close();

        storage.load();

        assertEquals(5, storage.objects.size());

        for(TestData td: storage.objects) {
            assertEquals(td, testData);
        }
    }

    @Test
    public void save() throws Exception {
        ObjectStorage<TestData> objectStorage = new ObjectStorage<>(TestData.class);

        TestData testData = new TestData();
        testData.i = 5;
        testData.f = 999.999f;
        testData.str = "hello world";

        objectStorage.objects.add(testData);
        objectStorage.objects.add(testData);
        objectStorage.objects.add(testData);
        objectStorage.objects.add(testData);
        objectStorage.objects.add(testData);

        objectStorage.save();

        Gson gson = new Gson();
        BufferedReader reader = Files.newBufferedReader(objectStorage.filePath);
        List<TestData> data = reader.lines()
                .map((line) -> gson.fromJson(line, TestData.class))
                .collect(Collectors.toList());

        assertEquals(5, data.size());

        for(TestData td: data) {
            assertEquals(td, testData);
        }
    }

}