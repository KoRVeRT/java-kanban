package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.exception.IntersectionException;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest {
    protected TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        taskManager = createTaskManager();
    }

    protected TaskManager createTaskManager() throws IOException, InterruptedException {
        return Managers.getInMemoryTaskManager();
    }

    @Test
    void updateTask_checkUpdateTaskWithSameStartTime() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(1, "Task3", TaskStatus.DONE, "01.01.2022-12:20", 0);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        //update
        taskManager.updateTask(task3);

        List<Task> expected = List.of(task3, task2);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void updateSubtask_checkUpdateTaskWithSameStartTime() {
        // create epics
        Epic epic = createEpic(1, "Epic", null, null, List.of());
        // create subtask
        Subtask subtask1 = createSubtask(2, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(2, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-15:25",
                45);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        //update
        taskManager.updateSubtask(subtask3);

        List<Task> expected = List.of(subtask3, subtask2);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void addSubtask_checkThrowIntersectionException_IfTimeOverlaps() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // create epics
        Epic epic = createEpic(3, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(4, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(5, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        // Intersection Subtask
        Subtask subtask3 = createSubtask(6, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-13:45",
                240);
        IntersectionException exception = assertThrows(IntersectionException.class,
                () -> taskManager.addSubtask(subtask3));
        assertEquals("Intersection between \"Subtask3\" and \"Subtask1\"", exception.getMessage());
    }

    @Test
    void updateSubtask_checkThrowIntersectionException_IfTimeOverlaps() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // create epics
        Epic epic = createEpic(3, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(4, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(5, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        // Intersection task
        Subtask subtask3 = createSubtask(4, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:10",
                240);

        IntersectionException exception = assertThrows(IntersectionException.class,
                () -> taskManager.updateSubtask(subtask3));
        assertEquals("Intersection between \"Subtask3\" and \"Subtask2\"", exception.getMessage());
    }

    @Test
    void addTask_checkThrowIntersectionException_IfTimeOverlaps() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // create epics
        Epic epic = createEpic(3, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(4, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(5, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(6, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // Intersection task
        Task task3 = createTask(7, "Task3", TaskStatus.DONE, "01.01.2022-16:05", 0);

        IntersectionException exception = assertThrows(IntersectionException.class,
                () -> taskManager.addTask(task3));
        assertEquals("Intersection between \"Task3\" and \"Subtask1\"", exception.getMessage());
    }

    @Test
    void updateTask_checkThrowIntersectionException_IfTimeOverlaps() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // create epics
        Epic epic = createEpic(3, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(4, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(5, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(6, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // Intersection task
        Task task3 = createTask(1, "Task3", TaskStatus.DONE, "01.01.2022-13:30", 40);

        IntersectionException exception = assertThrows(IntersectionException.class,
                () -> taskManager.updateTask(task3));
        assertEquals("Intersection between \"Task3\" and \"Task2\"", exception.getMessage());
    }

    @Test
    void getPrioritizedTasks_checkSortingTwoTasksWithoutStartTimeById_TasksShouldBeAtEndOfList() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, null, 0);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic = createEpic(4, "Epic", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(4, "Subtask1", epic.getId(), TaskStatus.NEW,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(4, "Subtask2", epic.getId(), TaskStatus.DONE,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", epic.getId(), TaskStatus.DONE,
                "01.01.2022-21:40",
                45);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> expected = List.of(task2, subtask1, subtask2, subtask3, task1, task3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getPrioritizedTasks_checkSortingTasksOfStartTime_AndOneTaskWithoutStartTimeShouldBeAtEndOfList() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic = createEpic(4, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(5, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(6, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(7, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> expected = List.of(task1, task2, subtask1, subtask2, subtask3, task3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteAllTasks_checkDeleteAllTasksInPrioritizedTasks() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic = createEpic(4, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(5, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(6, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(7, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // delete tasks
        taskManager.deleteAllTasks();

        List<Task> expected = List.of(subtask1, subtask2, subtask3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteAllSubtasks_checkDeleteAllSubtasksInPrioritizedTasks_AndCheckNewTimeOfEpic() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic = createEpic(4, "Epic", "01.01.2022-15:25", "01.01.2022-17:05", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(5, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(6, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(7, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // delete
        taskManager.deleteAllSubtasks();

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(0, epic.getDuration().toMinutes());
    }

    @Test
    void deleteAllEpics_checkDeleteAllSubtasksInPrioritizedTasks() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25",
                "01.01.2022-17:05", List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // delete
        taskManager.deleteAllEpics();

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_checkDeleteTaskInPrioritizedTasks() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        // delete task
        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of(task2);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubtaskById_checkDeleteSubtaskInPrioritizedTasks_AndChangeTimeOfEpic() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // delete subtask
        taskManager.deleteSubtaskById(subtask1.getId());

        LocalDateTime start = subtask2.getStartTime();
        LocalDateTime endTime = subtask3.getEndTime();
        Duration duration = Duration.from(subtask2.getDuration().plus(subtask3.getDuration()));
        assertEquals(start, epic.getStartTime());
        assertEquals(endTime, epic.getEndTime());
        assertEquals(duration, epic.getDuration());

        List<Task> expected = List.of(subtask2, subtask3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_checkDeleteEpicAndSubtasksOfEpicInPrioritizedTasks() {
        // create epic
        Epic epic1 = createEpic(1, "Epic1", null, null, List.of());
        Epic epic2 = createEpic(2, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(3, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(4, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(5, "Subtask3", 2, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // delete epic
        taskManager.deleteEpicById(epic1.getId());
        List<Task> expected = List.of(subtask3);
        List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_shouldGetTaskByUseIdFromTaskManager_AndSaveTaskInHistoryManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(task1, taskManager.getTaskById(task1.getId()));

        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getSubtaskById_shouldGetSubtaskByUseIdFromTaskManager_AndSaveSubtaskInHistoryManager() {
        // create epic
        Epic epic1 = createEpic(1, "Epic1", null, null, List.of());
        Epic epic2 = createEpic(2, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(3, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(4, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(5, "Subtask3", 2, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        assertEquals(subtask2, taskManager.getSubtaskById(subtask2.getId()));

        List<Task> expected = List.of(subtask1, subtask2);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getEpicById_shouldGetEpicByUseIdFromTaskManager_AndSaveEpicInHistoryManager() {
        // create epic
        Epic epic1 = createEpic(1, "Epic1", null, null, List.of());
        Epic epic2 = createEpic(2, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(3, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(4, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(5, "Subtask3", 2, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));

        List<Task> expected = List.of(epic1);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void addTask_shouldSaveTaskInTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void addSubtask_shouldSaveSubtaskInTaskManager_AndCheckAddSubtaskInEpic() {
        // create epic
        Epic epic = createEpic(1, "Epic", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Subtask> expectedSubtask = List.of(subtask1, subtask2, subtask3);
        List<Subtask> actualSubtask = taskManager.getAllSubTasks();
        assertEquals(expectedSubtask, actualSubtask);

        List<Subtask> expectedInEpic = List.of(subtask1, subtask2, subtask3);
        List<Subtask> actualInEpic = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(expectedInEpic, actualInEpic);
    }

    @Test
    void addEpic_shouldCheckEpicTimeInTaskManagerWithSubtasks() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", 1, TaskStatus.IN_PROGRESS,
                "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        LocalDateTime start = subtask1.getStartTime();
        LocalDateTime endTime = subtask3.getEndTime();
        Duration duration = Duration.from(subtask1.getDuration()).plus(subtask2.getDuration()
                .plus(subtask3.getDuration()));

        assertEquals(start, taskManager.getEpicById(epic.getId()).getStartTime());
        assertEquals(endTime, taskManager.getEpicById(epic.getId()).getEndTime());
        assertEquals(duration, taskManager.getEpicById(epic.getId()).getDuration());
    }

    @Test
    void addEpic_shouldCheckEpicTimeInTaskManagerWithoutSubtasks() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // add to manager
        taskManager.addEpic(epic);

        assertNull(taskManager.getEpicById(epic.getId()).getStartTime());
        assertEquals(0, taskManager.getEpicById(epic.getId()).getDuration().toMinutes());
        assertNull(taskManager.getEpicById(epic.getId()).getEndTime());
    }

    @Test
    void addEpic_shouldSaveEpicInTaskManagerWithStatusNew() {
        // create epic
        Epic epic1 = createEpic(1, "Epic1", null, null, List.of());
        Epic epic2 = createEpic(2, "Epic2", null, null, List.of());
        // add to manager
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);

        assertEquals(TaskStatus.NEW, epic2.getStatus());
        assertEquals(TaskStatus.NEW, epic2.getStatus());
    }

    @Test
    void addEpic_shouldSaveEpicInTaskManagerWithStatusInProgress() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.IN_PROGRESS, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.IN_PROGRESS, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", 1, TaskStatus.IN_PROGRESS, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Epic> expected = List.of(epic);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void addEpic_shouldSaveEpicInTaskManagerWithStatusDone() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.DONE, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(4, "Subtask3", 1, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Epic> expected = List.of(epic);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void updateTask_shouldUpdateTaskInTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(2, "Task3", TaskStatus.DONE, null, 0);
        // create epic
        Epic epic = createEpic(4, "Epic1", null, null, List.of());
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        // update
        taskManager.updateTask(task3);

        List<Task> expected = List.of(task1, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void updateSubtask_shouldUpdateSubtask_AndChangeStatusDoneOfEpicInTaskManager_AndChangeTime() {
        // create epic
        Epic epic = createEpic(1, "Epic1", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(2, "Subtask1", 1, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(3, "Subtask2", 1, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(2, "Subtask3", 1, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        // update
        taskManager.updateSubtask(subtask3);

        LocalDateTime start = subtask2.getStartTime();
        LocalDateTime endTime = subtask3.getEndTime();
        Duration duration = Duration.from(subtask3.getDuration()).plus(subtask2.getDuration());

        assertEquals(start, taskManager.getEpicById(epic.getId()).getStartTime());
        assertEquals(endTime, taskManager.getEpicById(epic.getId()).getEndTime());
        assertEquals(duration, taskManager.getEpicById(epic.getId()).getDuration());

        List<Subtask> expected = List.of(subtask3, subtask2);
        List<Subtask> actual = taskManager.getAllSubTasks();
        assertEquals(expected, actual);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void updateEpic_shouldUpdateEpicInTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(4, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(5, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(6, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(7, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // update
        taskManager.updateEpic(epic2);

        List<Epic> expected = List.of(epic2);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskByUseIdInTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete
        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of(task2, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubtaskById_shouldRemoveSubtaskByUseIdInTaskManager_ChangeEpicStatus_RemoveSubtaskFromEpic() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete
        taskManager.deleteSubtaskById(subtask1.getId());

        List<Subtask> expectedSubtask = List.of(subtask2, subtask3);
        List<Subtask> actualSubtask = taskManager.getAllSubTasks();
        assertEquals(expectedSubtask, actualSubtask);

        List<Subtask> expectedInEpic = List.of(subtask3);
        List<Subtask> actualInEpic = taskManager.getSubtasksByEpicId(epic1.getId());
        assertEquals(expectedInEpic, actualInEpic);

        assertEquals(TaskStatus.DONE, epic1.getStatus());
    }

    @Test
    void deleteEpicById_shouldRemoveEpicByUseIdInTaskManager_AndRemoveSubtasksOfEpic() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete
        taskManager.deleteEpicById(epic1.getId());

        List<Epic> expectedEpics = List.of(epic2);
        List<Epic> actualEpics = taskManager.getAllEpics();
        assertEquals(expectedEpics, actualEpics);

        List<Subtask> expectedSubtasks = List.of(subtask2);
        List<Subtask> actualSubtasks = taskManager.getAllSubTasks();
        assertEquals(expectedSubtasks, actualSubtasks);
    }

    @Test
    void getAllTasks_shouldReturnAllTasksFromTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 5, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = taskManager.getAllTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getAllSubtasks_shouldReturnAllSubtasksFromTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25",
                "01.01.2022-17:05", List.of());
        Epic epic2 = createEpic(5, "Epic2", null,
                null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 5, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Subtask> expected = List.of(subtask1, subtask2, subtask3);
        List<Subtask> actual = taskManager.getAllSubTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getAllEpics_shouldReturnAllEpicsFromTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 5, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getAllEpics();
        assertEquals(expected, actual);
    }

    @Test
    void getHistory_shouldReturnHistoryFromTaskManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 5, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());

        List<Task> expected = List.of(task2, subtask1, epic2);
        List<Task> actual = taskManager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void getSubtasksByEpicId_shouldReturnAllSubtasksFromEpic() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 5, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());

        List<Subtask> expected = List.of(subtask2, subtask3);
        List<Subtask> actual = taskManager.getSubtasksByEpicId(epic2.getId());
        assertEquals(expected, actual);
    }

    @Test
    void deleteAllTasks_shouldRemoveAllTasksFromTaskManager_AndHistoryManager() {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete all Tasks
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
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25", "01.01.2022-17:05",
                List.of());
        Epic epic2 = createEpic(5, "Epic2", null, null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete all Subtasks
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
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, null, 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", "01.01.2022-15:25",
                "01.01.2022-17:05", List.of());
        Epic epic2 = createEpic(5, "Epic2", null,
                null, List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 5, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        Subtask subtask3 = createSubtask(8, "Subtask3", 4, TaskStatus.DONE, "01.01.2022-21:40",
                240);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        // get from manager
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        // delete all Epics
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

    private Task createTask(int id, String name, TaskStatus status, String startTime, long duration) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setStatus(status);
        if (startTime != null) {
            task.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            task.setDuration(duration);
        }
        return task;
    }

    private Epic createEpic(int id, String name, String startTime, String endTime, List<Integer> subtaskId) {
        Epic epic = new Epic();
        epic.setId(id);
        epic.setName(name);
        if (startTime != null) {
            epic.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (endTime != null) {
            epic.setEndTime(LocalDateTime.parse(endTime, Task.FORMATTER_OF_DATE));
        }
        for (Integer number : subtaskId) {
            epic.addSubtaskId(number);
        }
        return epic;
    }

    private Subtask createSubtask(int id, String name, int epicId, TaskStatus status, String startTime, long duration) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setName(name);
        subtask.setEpicId(epicId);
        subtask.setStatus(status);
        if (startTime != null) {
            subtask.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            subtask.setDuration(duration);
        }
        return subtask;
    }
}