package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    @Override
    protected TaskManager createTaskManager() {
        return Managers.getFileBackedTasksManagerTest();
    }
    @BeforeEach
    void clearUp()  {
        try(Writer writer = new FileWriter(Managers.pathSaveFileFromTest,StandardCharsets.UTF_8)) {
            writer.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromFile_shouldCheckSaveAndLoadManagerFromFile() {
        Task task1 = new Task();
        task1.setName("Купить батон");
        task1.setDescription("Нужен свежий батон для бутербродов");
        task1.setStatus(TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 20));
        task1.setDuration(15);
        // create epics
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 6 спринту в Яндекс.Практикуме");
        // add tasks and Epics
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        // create subtasks
        Subtask subtask1 = new Subtask();
        subtask1.setName("Закончить тренажер");
        subtask1.setDescription("Выполнить все задания в тренажере");
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 20));
        subtask1.setDuration(55);

        Subtask subtask2 = new Subtask();
        subtask2.setName("Посмотреть вебинар");
        subtask2.setDescription("Итоговый вебинар по ТЗ спринта №6");
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.NEW);
        subtask2.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 17, 20));
        subtask2.setDuration(85);
        // add subtasks
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        // get tasks
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        FileBackedTasksManager fileLoad = Managers.loadFromFileTest();
        assertEquals(taskManager.getAllTasks(), fileLoad.getAllTasks());
        assertEquals(taskManager.getAllSubTasks(), fileLoad.getAllSubTasks());
        assertEquals(taskManager.getAllEpics(), fileLoad.getAllEpics());
        assertEquals(taskManager.getHistory(), fileLoad.getHistory());
    }

    @Test
    void loadFromFile_shouldCheckSaveAndLoadManagerFromFile_OnlyOneEpic() {
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 6 спринту в Яндекс.Практикуме");
        taskManager.addEpic(epic1);

        FileBackedTasksManager fileLoad = Managers.loadFromFileTest();
        assertEquals(taskManager.getAllTasks(), fileLoad.getAllTasks());
        assertEquals(taskManager.getAllSubTasks(), fileLoad.getAllSubTasks());
        assertEquals(taskManager.getAllEpics(), fileLoad.getAllEpics());
        assertEquals(taskManager.getHistory(), fileLoad.getHistory());
    }

    @Test
    void loadFromFile_shouldCheckSaveAndLoadManagerFromFile_EmptyTaskList() {
        FileBackedTasksManager fileLoad = Managers.loadFromFileTest();

        assertEquals(taskManager.getAllTasks(), fileLoad.getAllTasks());
        assertEquals(taskManager.getAllSubTasks(), fileLoad.getAllSubTasks());
        assertEquals(taskManager.getAllEpics(), fileLoad.getAllEpics());
        assertEquals(taskManager.getHistory(), fileLoad.getHistory());
    }

    @Test
    public void throwManagerSaveExceptionTest() {
        String filePath = "fileSave.txt";

        ManagerSaveException exception = assertThrows(ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(filePath));
        assertEquals("oops, error :|", exception.getMessage());
    }
}