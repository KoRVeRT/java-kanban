package ru.yandex.practicum.tasktracker.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.tasktracker.adapter.DurationAdapter;
import ru.yandex.practicum.tasktracker.adapter.LocalDateTimeAdapter;
import ru.yandex.practicum.tasktracker.service.HttpTaskManager;
import ru.yandex.practicum.tasktracker.server.KVServer;
import ru.yandex.practicum.tasktracker.service.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {
    public static final String PATH_SAVE_FILE = "resources/save-manager.csv";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager(getDefaultHistory(), "http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTasksManager() {
        return new FileBackedTasksManager(getDefaultHistory(), PATH_SAVE_FILE);
    }

    public static Gson getGson() {
        return gson;
    }

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager loadFromFile() {
        return FileBackedTasksManager.loadFromFile(PATH_SAVE_FILE);
    }
}