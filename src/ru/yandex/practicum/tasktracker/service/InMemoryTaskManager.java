package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generatorId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic currentEpic = epics.get(epicId);
        List<Subtask> subtasksOfCurrentEpic = new ArrayList<>();
        for (Integer subtaskId : currentEpic.getSubtaskIds()) {
            subtasksOfCurrentEpic.add(subTasks.get(subtaskId));
        }
        return subtasksOfCurrentEpic;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) { // Clear the list of subtasks for each epic and assign the status NEW.
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear(); // If you have removed Epic, then delete its subtasks.
    }

    @Override
    public Task getTaskById(int taskId) {
        Task currentTask = tasks.get(taskId);
        historyManager.add(currentTask);
        return currentTask;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        historyManager.add(currentSubtask);
        return currentSubtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic currentEpic = epics.get(epicId);
        historyManager.add(currentEpic);
        return currentEpic;
    }

    @Override
    public void addTask(Task task) {
        task.setId(generatorId);
        tasks.put(generatorId, task);
        generatorId++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic currentEpic = epics.get(subtask.getEpicId());
        subtask.setId(generatorId);
        subTasks.put(generatorId, subtask);
        currentEpic.addSubtaskId(generatorId);
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
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
        Epic currentEpic = epics.get(subtask.getEpicId());
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        Epic currentEpic = epics.get(currentSubtask.getEpicId());
        subTasks.remove(subtaskId);
        currentEpic.removeSubtaskId(subtaskId);
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
    }

    @Override
    public void deleteEpicById(int id) {
        Epic currentEpic = getEpicById(id);
        for (Integer subtaskId : currentEpic.getSubtaskIds()) {
            subTasks.remove(subtaskId); // If you have removed Epic, then delete its subtasks.
        }
        epics.remove(id);
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask currentSubtask = subTasks.get(subtaskId);
            TaskStatus currentSubtaskStatus = currentSubtask.getStatus();
            switch (currentSubtaskStatus) {
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
