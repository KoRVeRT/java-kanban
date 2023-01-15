package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.server.HttpTaskManager;
import ru.yandex.practicum.tasktracker.server.KVServer;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends InMemoryTaskManagerTest {
    private KVServer server;

    @Override
    protected TaskManager createTaskManager() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        return Managers.getDefault();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void loadFromServer() throws IOException, InterruptedException {
        Task task1 = createTask("01.01.2022-12:20", 45);
        Task task2 = createTask("01.01.2022-18:20", 15);
        Epic epic1 = createEpic(List.of());
        // create subtasks
        Subtask subtask1 = createSubtask("01.01.2022-15:25", 120);
        taskManager.addEpic(epic1);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addSubtask(subtask1);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        HttpTaskManager newManager = new HttpTaskManager(Managers.getDefaultHistory(),
                "http://localhost:" + KVServer.PORT);
        newManager.loadFromServer();
        assertEquals(taskManager.getAllTasks(), newManager.getAllTasks());
        assertEquals(taskManager.getAllSubTasks(), newManager.getAllSubTasks());
        assertEquals(taskManager.getAllEpics(), newManager.getAllEpics());
        assertEquals(taskManager.getHistory(), newManager.getHistory());
        assertEquals(taskManager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
    }

    private Task createTask(String startTime, long duration) {
        Task task = new Task();
        task.setName("Task");
        task.setStatus(TaskStatus.NEW);
        if (startTime != null) {
            task.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            task.setDuration(duration);
        }
        return task;
    }

    private Epic createEpic(List<Integer> subtaskId) {
        Epic epic = new Epic();
        epic.setName("Epic");
        epic.setStatus(TaskStatus.NEW);
        for (Integer number : subtaskId) {
            epic.addSubtaskId(number);
        }
        return epic;
    }

    private Subtask createSubtask(String startTime, long duration) {
        Subtask subtask = new Subtask();
        subtask.setName("Subtask");
        subtask.setEpicId(1);
        subtask.setStatus(TaskStatus.NEW);
        if (startTime != null) {
            subtask.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            subtask.setDuration(duration);
        }
        return subtask;
    }
}
