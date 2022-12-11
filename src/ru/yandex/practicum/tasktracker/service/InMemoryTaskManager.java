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
    protected int generatorId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager;

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
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.keySet().forEach(historyManager::remove);
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        });
        subTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        addInHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subTasks.get(subtaskId);
        addInHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        addInHistory(epic);
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
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    public void addInHistory(Task task){
        historyManager.add(task);
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subTasks.get(subtaskId);
            TaskStatus subtaskStatus = subtask.getStatus();
            switch (subtaskStatus) {
                case NEW -> isDone = false;
                case IN_PROGRESS -> {
                    isNew = false;
                    isDone = false;
                }
                case DONE -> isNew = false;
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