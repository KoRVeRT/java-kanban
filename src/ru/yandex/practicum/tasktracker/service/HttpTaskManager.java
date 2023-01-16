package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.server.KVTaskClient;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    static final String KEY_TASKS = "tasks";
    static final String KEY_SUBTASKS = "subtasks";
    static final String KEY_EPICS = "epics";
    static final String KEY_HISTORY = "history";
    private final KVTaskClient client;
    private static final Gson gson = Managers.getGson();

    public HttpTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager, path);
        client = new KVTaskClient(path);
    }

    public void loadFromServer() {

        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                ++generatorId;
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                ++generatorId;
                epics.put(task.getId(), task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                ++generatorId;
                subtasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                historyManager.add(searchTask(jsonTaskId.getAsInt()));
            }
        }
    }

    @Override
    public void save() {
        client.put(KEY_TASKS, gson.toJson(tasks.values()));
        client.put(KEY_SUBTASKS, gson.toJson(subtasks.values()));
        client.put(KEY_EPICS, gson.toJson(epics.values()));
        client.put(KEY_HISTORY, gson.toJson(getHistory()
                .stream()
                .map(Task::getId)
                .toList()));
    }
}