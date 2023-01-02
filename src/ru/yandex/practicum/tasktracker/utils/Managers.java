package ru.yandex.practicum.tasktracker.utils;

import ru.yandex.practicum.tasktracker.service.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

public final class Managers {
    public static String pathSaveFile = "db/saveManager.csv";
    public static TaskManager getDefault() {
         return new FileBackedTasksManager(getDefaultHistory(), pathSaveFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTasksManager() {
        return new FileBackedTasksManager(getDefaultHistory(), pathSaveFile);
    }

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static FileBackedTasksManager loadFromFile() {
        return FileBackedTasksManager.loadFromFile(pathSaveFile);
    }
}