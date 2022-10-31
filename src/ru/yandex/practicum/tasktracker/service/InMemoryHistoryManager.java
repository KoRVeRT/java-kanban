package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistory = new ArrayList<>();

    @Override
    public  void add(Task task) {
        if (viewHistory.size() < 10) {
            viewHistory.add(task);
        } else {
            viewHistory.remove(0);
            viewHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory;
    }
}
