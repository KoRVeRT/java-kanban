package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryServiceTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        Task task1 = Task(1);
        Task task2 = Task(2);
        Task task3 = Task(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        // your test code here
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        // your test code here
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        // your test code here
    }

    private static Task Task(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }

}
