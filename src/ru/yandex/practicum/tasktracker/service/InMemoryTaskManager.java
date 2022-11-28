package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generatorId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            epicSubtasks.add(subTasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (Epic epic : epics.values()) { // Clear the list of subtasks for each epic and assign the status NEW.
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        }
        subTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int epicId : epics.keySet()) {
            Epic epic = getEpicById(epicId);
            for (int subtaskId : epic.getSubtaskIds()){
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epicId);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subTasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addTask(Task task) {
        task.setId(generatorId);
        tasks.put(generatorId, task);
        generatorId++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(generatorId);
        subTasks.put(generatorId, subtask);
        epic.addSubtaskId(generatorId);
        epic.setStatus(calculateEpicStatus(epic));
        generatorId++;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generatorId);
        epics.put(generatorId, epic);
        epic.setStatus(TaskStatus.NEW);
        generatorId++;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setStatus(calculateEpicStatus(epic));
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.setStatus(calculateEpicStatus(epic));
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subTasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        subTasks.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        epic.setStatus(calculateEpicStatus(epic));
        historyManager.remove(subtaskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = getEpicById(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subTasks.get(subtaskId);
            TaskStatus subtaskStatus = subtask.getStatus();
            switch (subtaskStatus) {
                case NEW:
                    isDone = false;
                    break;
                case IN_PROGRESS:
                    isNew = false;
                    isDone = false;
                    break;
                case DONE:
                    isNew = false;
            }
        }

        if (!isNew && !isDone) {
            return TaskStatus.IN_PROGRESS;
        } else if (isNew && !isDone) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.DONE;
        }
    }
}