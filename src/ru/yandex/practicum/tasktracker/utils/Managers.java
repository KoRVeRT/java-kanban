package ru.yandex.practicum.tasktracker.utils;

import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

public final class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}