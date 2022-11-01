package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistory = new LinkedList<>();
    private final int COUNT_RECORDS = 10;

    @Override
    public void add(Task task) {
        if (viewHistory.size() == COUNT_RECORDS) {
            viewHistory.remove(0);
        }
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory;
    }
}