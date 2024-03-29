package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.service.exception.ManagerSaveException;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String CSV_FILE_HEADER = "id,type,name,status,description,startTime,duration(min)," +
            "endTime,epic";
    private static final String CSV_VALUE_SEPARATOR = ",";
    private final String pathSave;

    public FileBackedTasksManager(HistoryManager historyManager, String path) {
        super(historyManager);
        this.pathSave = path;
    }

    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String taskLine = reader.readLine();
                // removing the table header or check that there is a header and the file is not empty
                if (taskLine.equals(CSV_FILE_HEADER)) {
                    taskLine = reader.readLine();
                }
                if (taskLine.isBlank()) {
                    String idTasksHistory = reader.readLine();
                    if (idTasksHistory != null) {
                        tasksManager.recoverHistory(historyFromString(idTasksHistory));
                    }
                    break;
                }
                tasksManager.recoverTask(tasksManager.fromString(taskLine));
            }
            return tasksManager;
        } catch (IOException e) {
            throw new ManagerSaveException("File to download not found", e);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    protected Task searchTask(Integer task) {
        if (tasks.containsKey(task)) {
            return tasks.get(task);
        } else if (subtasks.containsKey(task)) {
            return subtasks.get(task);
        } else {
            return epics.get(task);
        }
    }

    protected void save() {
        try (Writer writer = new FileWriter(pathSave, StandardCharsets.UTF_8)) {
            writer.write(CSV_FILE_HEADER + "\n");
            for (int i = 1; i <= generatorId; i++) {
                Task task = tasks.get(i);
                Epic epic = epics.get(i);
                Subtask subtask = subtasks.get(i);
                if (task != null) {
                    writer.write(task.toCsvRow() + "\n");
                } else if (epic != null) {
                    writer.write(epic.toCsvRow() + "\n");
                } else if (subtask != null) {
                    writer.write(subtask.toCsvRow() + "\n");
                }
            }
            writer.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void recoverHistory(List<Integer> idTasksHistory) {
        for (Integer task : idTasksHistory) {
            historyManager.add(searchTask(task));
        }
    }

    private void recoverTask(Task task) {
        switch (task.getType()) {
            case TASK -> {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
            case SUBTASK -> {
                subtasks.put(task.getId(), (Subtask) task);
                prioritizedTasks.add(task);
            }
            case EPIC -> epics.put(task.getId(), (Epic) task);
        }
        if (task.getId() > generatorId) {
            generatorId = task.getId();
        }
    }

    private static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(CSV_VALUE_SEPARATOR));
    }

    private static List<Integer> historyFromString(String history) {
        return Arrays.stream(history.split(CSV_VALUE_SEPARATOR))
                .map(Integer::valueOf)
                .toList();
    }

    private Task fromString(String value) {
        Task task = new Task();
        String[] arrayDataTask = value.split(",");
        int idTask = Integer.parseInt(arrayDataTask[0]);
        TaskType typeTask = TaskType.valueOf(arrayDataTask[1]);
        String nameTask = arrayDataTask[2];
        TaskStatus statusTask = TaskStatus.valueOf(arrayDataTask[3]);
        String descriptionTask = arrayDataTask[4].equals("null") ? null : arrayDataTask[4];
        LocalDateTime startTime = arrayDataTask[5].equals("null") ? null : LocalDateTime.parse(arrayDataTask[5]);
        long duration = Long.parseLong(arrayDataTask[6]);
        String endTime = arrayDataTask[7];

        switch (typeTask) {
            case TASK -> {
                task.setId(idTask);
                task.setName(nameTask);
                task.setStatus(statusTask);
                task.setDescription(descriptionTask);
                task.setStartTime(startTime);
                task.setDuration(duration);
            }
            case SUBTASK -> {
                Subtask subtask = new Subtask();
                subtask.setId(idTask);
                subtask.setName(nameTask);
                subtask.setStatus(statusTask);
                subtask.setDescription(descriptionTask);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                int epicId = Integer.parseInt(arrayDataTask[8]);
                subtask.setEpicId(epicId);
                Epic epic = epics.get(epicId);
                epic.addSubtaskId(subtask.getId());
                task = subtask;
            }
            case EPIC -> {
                Epic epic = new Epic();
                epic.setId(idTask);
                epic.setName(nameTask);
                epic.setStatus(statusTask);
                epic.setDescription(descriptionTask);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                if (!(endTime.equals("null"))) {
                    epic.setEndTime(LocalDateTime.parse(endTime));
                }
                task = epic;
            }
        }
        return task;
    }
}