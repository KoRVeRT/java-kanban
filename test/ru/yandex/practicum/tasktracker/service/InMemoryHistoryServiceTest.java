package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryServiceTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

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
    void remove_shouldReturnEmptyHistory_ifDeleteSingleTask() {
        Task task1 = Task(1);
        historyManager.add(task1);

        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void remove_shouldReturnHistoryWithoutEpicSubtasks_ifDeleteEpic() {
        Task task1 = Task(1);
        Epic epic1 = Epic(2);
        Epic epic2 = Epic(3);
        Subtask subtask1 = Subtask(4, 2);
        Subtask subtask2 = Subtask(5, 2);
        Subtask subtask3 = Subtask(6, 3);
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> expected = List.of(task1,epic2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.deleteEpicById(epic1.getId());
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    private static Task Task(int id) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(TaskStatus.NEW);
        return task;
    }

    private static Epic Epic(int id) {
        Epic epic = new Epic();
        epic.setId(id);
        return epic;
    }

    private static Subtask Subtask(int id, int epicId) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setEpicId(epicId);
        subtask.setStatus(TaskStatus.NEW);
        return subtask;
    }
}