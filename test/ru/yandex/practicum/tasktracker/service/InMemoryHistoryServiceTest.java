package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Task task1 = Task(1);
        Task task2 = Task(2);
        Task task3 = Task(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        assertEquals(historyManager.getHistory().size(), 3);
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        Task task1 = Task(1);
        Task task2 = Task(2);
        Task task3 = Task(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        assertFalse(historyManager.getHistory().contains(task2));
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        Task task1 = Task(1);
        Task task2 = Task(2);
        Task task3 = Task(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.add(task1);
        assertEquals(historyManager.getHistory().indexOf(task1), 2);
    }

    @Test
    void remove_shouldReturnEmptyHistory_ifDeleteSingleTask() {
        Task task1 = Task(1);
        historyManager.add(task1);

        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void remove_shouldReturnCorrectHistory_ifDeleteTaskNotAddInHistory() {
        Task task1 = Task(1);
        Task task2 = Task(2);
        Task task3 = Task(3);
        Task task4 = Task(4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        historyManager.remove(task4.getId());
        List<Task> actual = historyManager.getHistory();
        assertEquals(expected, actual);
    }

    private static Task Task(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }
}