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
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private static final String HEADER_TABLE_IN_FILE = "id,type,name,status,description,startTime,duration(min)," +
            "endTime,epic";
    private final String pathSave;

    public FileBackedTasksManager(HistoryManager historyManager, String path) {
        super(historyManager);
        this.pathSave = path;
    }

    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
            // removing the table header or check that there is a header and the file is not empty
            reader.readLine();
            while (reader.ready()) {
                String taskLine = reader.readLine();
                if (taskLine.isBlank()) {
                    String idTasksHistory = reader.readLine();
                    if (idTasksHistory == null) {
                        break;
                    }
                    loadTasksManager.recoverHistory(historyFromString(idTasksHistory));
                    break;
                }
                loadTasksManager.recoverTask(loadTasksManager.fromString(taskLine));
            }
            return loadTasksManager;
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

    private void recoverHistory(List<Integer> IdTasksHistory) {
        for (Integer task : IdTasksHistory) {
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
                subTasks.put(task.getId(), (Subtask) task);
                prioritizedTasks.add(task);
            }
            case EPIC -> epics.put(task.getId(), (Epic) task);
        }
        if (task.getId() > generatorId) {
            generatorId = task.getId();
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringJoiner joiner = new StringJoiner(",");
        for (Task task : manager.getHistory()) {
            joiner.add(String.valueOf(task.getId()));
        }
        return joiner.toString();
    }

    private static List<Integer> historyFromString(String history) {
        List<Integer> idTasksHistory = new ArrayList<>();
        String[] tasks = history.split(",");
        for (String task : tasks) {
            idTasksHistory.add(Integer.valueOf(task));
        }
        return idTasksHistory;
    }

    private void save() {
        try (Writer writer = new FileWriter(pathSave, StandardCharsets.UTF_8)) {
            writer.write(HEADER_TABLE_IN_FILE + "\n");
            for (int i = 1; i <= generatorId; i++) {
                Task task = tasks.get(i);
                if (task != null) {
                    writer.write(task.toCsvRow() + "\n");
                    continue;
                }
                Epic epic = epics.get(i);
                if (epic != null) {
                    writer.write(epic.toCsvRow() + "\n");
                    continue;
                }
                Subtask subtask = subTasks.get(i);
                if (subtask != null) {
                    writer.write(subtask.toCsvRow() + "\n");
                }
            }
            writer.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
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

    private Task searchTask(Integer task) {
        if (tasks.containsKey(task)) {
            return tasks.get(task);
        } else if (subTasks.containsKey(task)) {
            return subTasks.get(task);
        } else {
            return epics.get(task);
        }
    }
}