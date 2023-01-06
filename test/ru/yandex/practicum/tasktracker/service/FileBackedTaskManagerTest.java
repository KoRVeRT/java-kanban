package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.exceptions.ManagerSaveException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    public static String pathSaveFileFromTest = "resources/saveManagerTest.csv";

    @Override
    protected TaskManager createTaskManager() {
        File file = new File(pathSaveFileFromTest);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return FileBackedTasksManager.loadFromFile(pathSaveFileFromTest);
    }

    @AfterEach
    void clearUp() throws IOException {
        Writer writer = new FileWriter(pathSaveFileFromTest, StandardCharsets.UTF_8);
        writer.write("");
    }

    @Test
    void loadFromFile_shouldCheckSaveAndLoadManagerFromFile() throws IOException {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, "null", 0);
        // create epics
        Epic epic = createEpic(4, "Epic");
        // create subtasks
        Subtask subtask1 = createSubtask(5, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(6, "Subtask2", 4, TaskStatus.NEW, "01.01.2022-18:25",
                45);
        Writer writer = new FileWriter(pathSaveFileFromTest, StandardCharsets.UTF_8);
        writer.write("id,type,name,status,description,startTime,duration(min),endTime,epic\n");
        writer.write("1,TASK,Task1,NEW,null,2022-01-01T12:20,15,2022-01-01T12:35\n");
        writer.write("2,TASK,Task2,IN_PROGRESS,null,2022-01-01T13:35,25,2022-01-01T14:00\n");
        writer.write("3,TASK,Task3,DONE,null,null,0,null\n");
        writer.close();
        /*
        id,type,name,status,description,startTime,duration(min),endTime,epic
2,TASK,Выбросить мусор,IN_PROGRESS,С этим делом лучше не медлить,2022-01-01T13:25,15,2022-01-01T13:40
3,TASK,Спать,IN_PROGRESS,Срочно!,2022-01-01T12:30,50,2022-01-01T13:20
6,TASK,Купить колбасу,NEW,Нужна докторская,2022-01-05T12:20,25,2022-01-05T12:45
1,EPIC,Сделать ТЗ.,IN_PROGRESS,Итоговое ТЗ по 6 спринту в Яндекс.Практикуме,2022-01-01T21:20,242,2022-01-02T01:57
7,EPIC,Приготовить ужин.,IN_PROGRESS,Ужин на двоих,2022-01-05T18:20,45,2022-01-05T19:05
4,SUBTASK,Посмотреть вебинар,NEW,Итоговый вебинар по ТЗ спринта №6,2022-01-01T23:55,122,2022-01-02T01:57,1
5,SUBTASK,Закончить тренажер,IN_PROGRESS,Выполнить все задания в тренажере,2022-01-01T21:20,120,2022-01-01T23:20,1
8,SUBTASK,Сходить за продуктами,IN_PROGRESS,Купить продукты для приготовления,2022-01-05T18:20,45,2022-01-05T19:05,7

1,4,5,3
        */

//        FileBackedTasksManager fileLoad = FileBackedTasksManager.loadFromFile(pathSaveFileFromTest);
//        assertEquals(taskManager.getAllTasks(), fileLoad.getAllTasks());
//        assertEquals(taskManager.getAllSubTasks(), fileLoad.getAllSubTasks());
//        assertEquals(taskManager.getAllEpics(), fileLoad.getAllEpics());
//        assertEquals(taskManager.getHistory(), fileLoad.getHistory());
    }

    @Test
    public void loadFromFile_throwManagerSaveExceptionTestIfFileMissing() {
        String filePath = "fileSave.csv";

        ManagerSaveException exception = assertThrows(ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(filePath));
        assertEquals("oops, error :|", exception.getMessage());
    }

    private Task createTask(int id, String name, TaskStatus status, String startTime, long duration) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setStatus(status);
        if (!(startTime.equals("null"))) {
            task.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        task.setDuration(duration);
        return task;
    }

    private Epic createEpic(int id, String name) {
        Epic epic = new Epic();
        epic.setId(id);
        epic.setName(name);
        return epic;
    }

    private Subtask createSubtask(int id, String name, int epicId, TaskStatus status, String startTime, long duration) {
        Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setName(name);
        subtask.setEpicId(epicId);
        subtask.setStatus(status);
        if (!(startTime.equals("null"))) {
            subtask.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        subtask.setDuration(duration);
        return subtask;
    }
}