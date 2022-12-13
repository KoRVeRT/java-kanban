package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private final TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

    @Test
    void getTaskById_shouldGetTaskByUseIdFromTaskManager_AndSaveTaskInHistoryManager() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task();
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        assertEquals(task1, taskManager.getTaskById(task1.getId()));

        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getSubtaskById_shouldGetSubtaskByUseIdFromTaskManager_AndSaveSubtaskInHistoryManager() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));

        List<Task> expected = List.of(subtask1);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getEpicById_shouldGetEpicByUseIdFromTaskManager_AndSaveEpicInHistoryManager() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));

        List<Task> expected = List.of(epic1);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void addTask_shouldSaveTaskInTaskManager() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task();
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void addSubtask_shouldSaveSubtaskInTaskManager_AndCheckSubtaskAddInEpic() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        List<Subtask> expectedSubtask = List.of(subtask1, subtask2);
        List<Subtask> actualSubtask = taskManager.getAllSubTasks();
        assertEquals(expectedSubtask, actualSubtask);

        List<Subtask> expectedInEpic = List.of(subtask1, subtask2);
        List<Subtask> actualInEpic = taskManager.getSubtasksByEpicId(epic1.getId());
        assertEquals(expectedInEpic, actualInEpic);
    }

    @Test
    void addEpic_shouldSaveEpicInTaskManagerWithStatusNew() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);

        assertEquals(TaskStatus.NEW, epic2.getStatus());
        assertEquals(TaskStatus.NEW, epic2.getStatus());
    }

    @Test
    void updateTask_shouldUpdateTaskInTaskManager() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task();
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        Task task3 = new Task();
        task3.setId(task2.getId());
        task3.setStatus(TaskStatus.NEW);
        taskManager.updateTask(task3);

        List<Task> expected = List.of(task1, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void updateSubtask_shouldUpdateSubtask_AndChangeStatusOfEpicInTaskManager() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setId(subtask1.getId());
        subtask3.setEpicId(epic1.getId());
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);

        List<Subtask> expected = List.of(subtask3, subtask2);
        List<Subtask> actual = taskManager.getAllSubTasks();
        assertEquals(expected, actual);
        assertEquals(TaskStatus.DONE, epic1.getStatus());
    }

    @Test
    void updateEpic_shouldUpdateEpicInTaskManager() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Epic epic3 = new Epic();
        epic3.setId(epic2.getId());
        taskManager.updateEpic(epic3);

        List<Epic> expected = List.of(epic1, epic3);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskByUseIdInTaskManager() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task();
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        taskManager.deleteTaskById(task1.getId());
        List<Task> expected = List.of(task2);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubtaskById_shouldRemoveSubtaskByUseIdInTaskManager_ChangeEpicStatus_SubtaskRemoveFromEpic() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtaskById(subtask1.getId());
        List<Subtask> expectedSubtask = List.of(subtask2);
        List<Subtask> actualSubtask = taskManager.getAllSubTasks();
        assertEquals(expectedSubtask, actualSubtask);

        List<Subtask> expectedInEpic = List.of(subtask2);
        List<Subtask> actualInEpic = taskManager.getSubtasksByEpicId(epic1.getId());
        assertEquals(expectedInEpic, actualInEpic);

        assertEquals(TaskStatus.DONE, epic1.getStatus());
    }

    @Test
    void deleteEpicById_shouldRemoveEpicByUseIdInTaskManager_AndRemoveSubtasksOfEpic() {
        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic2.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(epic1.getId());
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask3);

        taskManager.deleteEpicById(epic1.getId());
        List<Epic> expectedEpics = List.of(epic2);
        List<Epic> actualEpics = taskManager.getAllEpics();
        assertEquals(expectedEpics, actualEpics);
        List<Subtask> expectedSubtasks = List.of(subtask1, subtask2);
        List<Subtask> actualSubtasks = taskManager.getAllSubTasks();
        assertEquals(expectedSubtasks, actualSubtasks);
    }

    @Test
    void getAllTasks_shouldReturnAllTasksFromTaskManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);
        Task task3 = new Task();
        taskManager.addTask(task3);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getAllSubtasks_shouldReturnAllSubtasksFromTaskManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        List<Subtask> expected = List.of(subtask1, subtask2);
        List<Subtask> actual = taskManager.getAllSubTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getAllEpics_shouldReturnAllEpicsFromTaskManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);
    }

    @Test
    void getHistory_shouldReturnHistoryFromTaskManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());

        List<Task> expected = List.of(task2, subtask1, epic2);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getSubtasksByEpicId_shouldReturnAllSubtasksFromEpic() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(epic2.getId());
        subtask3.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask3);

        List<Subtask> expected = List.of(subtask2, subtask3);
        List<Subtask> actual = taskManager.getSubtasksByEpicId(epic2.getId());
        assertEquals(expected, actual);
    }

    @Test
    void deleteAllTasks_shouldRemoveAllTasksFromTaskManager_AndHistoryManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(epic2.getId());
        subtask3.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.deleteAllTasks();


        List<Task> actualHistoryManager = taskManager.getHistory();
        List<Task> expectedHistoryManager = List.of(subtask1, epic2);
        assertEquals(expectedHistoryManager, actualHistoryManager);

        List<Task> actualTasks = taskManager.getAllTasks();
        List<Task> expectedTasks = List.of();
        assertEquals(expectedTasks, actualTasks);
    }

    @Test
    void deleteAllSubtasks_shouldRemoveAllSubtaskFromTaskManager_AndHistoryManager_AndChangeEpicsStatusForNew() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(epic2.getId());
        subtask3.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.deleteAllSubtasks();


        List<Task> actualHistoryManager = taskManager.getHistory();
        List<Task> expectedHistoryManager = List.of(task2, task1, epic2);
        assertEquals(expectedHistoryManager, actualHistoryManager);

        List<Subtask> actualSubtasks = taskManager.getAllSubTasks();
        List<Subtask> expectedTSubtasks = List.of();
        assertEquals(expectedTSubtasks, actualSubtasks);

        assertEquals(TaskStatus.NEW, epic2.getStatus());
        assertEquals(TaskStatus.NEW, epic2.getStatus());
    }

    @Test
    void deleteAllEpics_shouldRemoveAllEpics_IncludingThemSubtaskFromTaskManager_AndHistoryManager() {
        Task task1 = new Task();
        taskManager.addTask(task1);
        Task task2 = new Task();
        taskManager.addTask(task2);

        Epic epic1 = new Epic();
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic();
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setEpicId(epic2.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setEpicId(epic2.getId());
        subtask3.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.deleteAllEpics();


        List<Task> actualHistoryManager = taskManager.getHistory();
        List<Task> expectedHistoryManager = List.of(task2, task1);
        assertEquals(expectedHistoryManager, actualHistoryManager);

        List<Epic> actualEpics = taskManager.getAllEpics();
        List<Epic> expectedEpics = List.of();
        assertEquals(expectedEpics, actualEpics);

        List<Subtask> actualSubtasks = taskManager.getAllSubTasks();
        List<Subtask> expectedSubtasks = List.of();
        assertEquals(expectedSubtasks, actualSubtasks);
    }
}