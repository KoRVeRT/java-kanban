package ru.yandex.practicum.tasktracker.utils;

import ru.yandex.practicum.tasktracker.service.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new InMemoryHistoryManager(), pathSaveFile());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static String pathSaveFile(){
        return "db/saveManager.csv";
    }

    public static String pathFolder(){
        return "db";
    }
}