package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

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
        writer.close();
    }

    @Test
    void loadFromFile_shouldCheckLoadManagerFromFile() throws IOException {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, "null", 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", TaskStatus.IN_PROGRESS, "01.01.2022-15:25", 120,
                "01.01.2022-17:05", List.of(6, 7));
        Epic epic2 = createEpic(5, "Epic2", TaskStatus.NEW, "null", 0,
                "null", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        // write to file
        Writer writer = new FileWriter(pathSaveFileFromTest, StandardCharsets.UTF_8);
        writer.write("id,type,name,status,description,startTime,duration(min),endTime,epic\n");
        writer.write("1,TASK,Task1,NEW,null,2022-01-01T12:20,15,2022-01-01T12:35\n");
        writer.write("2,TASK,Task2,IN_PROGRESS,null,2022-01-01T13:35,25,2022-01-01T14:00\n");
        writer.write("3,TASK,Task3,DONE,null,null,0,null\n");
        writer.write("4,EPIC,Epic1,IN_PROGRESS,null,2022-01-01T15:25,120,2022-01-01T19:10\n");
        writer.write("5,EPIC,Epic2,NEW,null,null,0,null\n");
        writer.write("6,SUBTASK,Subtask1,NEW,null,2022-01-01T15:25,75,2022-01-01T16:40,4\n");
        writer.write("7,SUBTASK,Subtask2,DONE,null,2022-01-01T18:25,45,2022-01-01T19:10,4\n");
        writer.write("\n1,4,6,3");
        writer.close();

        FileBackedTasksManager fileLoadManager = FileBackedTasksManager.loadFromFile(pathSaveFileFromTest);
        assertEquals(List.of(task1, task2, task3), fileLoadManager.getAllTasks());
        assertEquals(List.of(subtask1, subtask2), fileLoadManager.getAllSubTasks());
        assertEquals(List.of(epic1, epic2), fileLoadManager.getAllEpics());
        assertEquals(List.of(task1, epic1, subtask1, task3), fileLoadManager.getHistory());
    }

    @Test
    void loadFromFile_shouldCheckSaveManagerFromFile() throws IOException {
        // create task
        Task task1 = createTask(1, "Task1", TaskStatus.NEW, "01.01.2022-12:20", 15);
        Task task2 = createTask(2, "Task2", TaskStatus.IN_PROGRESS, "01.01.2022-13:35", 25);
        Task task3 = createTask(3, "Task3", TaskStatus.DONE, "null", 0);
        // create epics
        Epic epic1 = createEpic(4, "Epic1", TaskStatus.IN_PROGRESS, "01.01.2022-15:25", 120,
                "01.01.2022-17:05", List.of());
        Epic epic2 = createEpic(5, "Epic2", TaskStatus.NEW, "null", 0,
                "null", List.of());
        // create subtasks
        Subtask subtask1 = createSubtask(6, "Subtask1", 4, TaskStatus.NEW, "01.01.2022-15:25",
                75);
        Subtask subtask2 = createSubtask(7, "Subtask2", 4, TaskStatus.DONE, "01.01.2022-18:25",
                45);
        // add to manager
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        // get from manager
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task3.getId());
        // Read text from file
        StringBuilder actual = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathSaveFileFromTest, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isBlank()) {
                    line = reader.readLine();
                    actual.append("\n").append(line);
                    break;
                }
                actual.append(line).append("\n");
            }
        }
        String expected = """
                id,type,name,status,description,startTime,duration(min),endTime,epic
                1,TASK,Task1,NEW,null,2022-01-01T12:20,15,2022-01-01T12:35
                2,TASK,Task2,IN_PROGRESS,null,2022-01-01T13:35,25,2022-01-01T14:00
                3,TASK,Task3,DONE,null,null,0,null
                4,EPIC,Epic1,IN_PROGRESS,null,2022-01-01T15:25,120,2022-01-01T19:10
                5,EPIC,Epic2,NEW,null,null,0,null
                6,SUBTASK,Subtask1,NEW,null,2022-01-01T15:25,75,2022-01-01T16:40,4
                7,SUBTASK,Subtask2,DONE,null,2022-01-01T18:25,45,2022-01-01T19:10,4

                1,4,6,3""";

        assertEquals(expected, actual.toString());
    }

    @Test
    public void loadFromFile_throwManagerSaveExceptionTestIfFileMissing() {
        String filePath = "fileSave.csv";

        ManagerSaveException exception = assertThrows(ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(filePath));
        assertEquals("File to download not found", exception.getMessage());
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

    private Epic createEpic(int id, String name, TaskStatus status, String startTime, long duration, String endTime,
                            List<Integer> subtaskId) {
        Epic epic = new Epic();
        epic.setId(id);
        epic.setName(name);
        epic.setStatus(status);
        if (!(startTime.equals("null"))) {
            epic.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        epic.setDuration(duration);
        if (!(endTime.equals("null"))) {
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
        if (!(startTime.equals("null"))) {
            subtask.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        subtask.setDuration(duration);
        return subtask;
    }
}