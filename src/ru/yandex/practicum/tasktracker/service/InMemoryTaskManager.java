package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int generatorId;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    private boolean checkIntersections(Task task) {
        // if the task is without start time, then do not check
        if (task.getStartTime() == null) {
            return true;
        }
        Task preTask = prioritizedTasks.lower(task);
        Task afterTask = prioritizedTasks.higher(task);
        if (preTask == null && afterTask == null) {
            return true;
        }
        // if the next task has no start time do not check
        if (preTask == null && afterTask.getStartTime() == null) {
            return true;
        }
        if (preTask == null) {
            return !task.getEndTime().isAfter(afterTask.getStartTime());
        }
        if (afterTask == null || afterTask.getStartTime() == null) {
            return !task.getStartTime().isBefore(preTask.getEndTime());
        }
        return !task.getEndTime().isAfter(afterTask.getStartTime())
                || !task.getStartTime().isBefore(preTask.getEndTime());
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
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.TASK);
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.keySet().forEach(historyManager::remove);
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setDuration(0);
            epic.setEndTime(null);
        });
        subTasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.SUBTASK);
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.SUBTASK);
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
        ++generatorId;
        task.setId(generatorId);
        if (!(checkIntersections(task))) {
            --generatorId;
            return;
        }
        tasks.put(generatorId, task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        ++generatorId;
        subtask.setId(generatorId);
        if (!(checkIntersections(subtask))) {
            --generatorId;
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        subTasks.put(generatorId, subtask);
        epic.addSubtaskId(generatorId);
        calculateEpicParameters(epic);
        addToPrioritizedTasks(subtask);
    }

    @Override
    public void addEpic(Epic epic) {
        ++generatorId;
        epic.setId(generatorId);
        epics.put(generatorId, epic);
        epic.setStatus(TaskStatus.NEW);
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.removeIf(taskDelete -> taskDelete.getId() == task.getId());
        if (!(checkIntersections(task))) {
            return;
        }
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        calculateEpicParameters(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        prioritizedTasks.removeIf(subtaskDelete -> subtaskDelete.getId() == subtask.getId());
        if (!(checkIntersections(subtask))) {
            return;
        }
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        calculateEpicParameters(epic);
        addToPrioritizedTasks(subtask);
    }

    @Override
    public void deleteTaskById(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        prioritizedTasks.remove(subTasks.get(subtaskId));
        Subtask subtask = subTasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        subTasks.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        calculateEpicParameters(epic);
        historyManager.remove(subtaskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            prioritizedTasks.remove(subTasks.get(subtaskId));
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    protected void calculateEpicParameters(Epic epic) {
        calculateEpicStatus(epic);
        calculateEpicTime(epic);
    }

    private void calculateEpicStatus(Epic epic) {
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
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (isNew && !isDone) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.DONE);
        }
    }

    private void calculateEpicTime(Epic epic) {
        LocalDateTime start = null;
        LocalDateTime endTime = null;
        Duration epicNewDuration = Duration.ofMinutes(0);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subTasks.get(subtaskId);
            if (subtask.getStartTime() == null) {
                continue;
            }
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            long subtaskDuration = subtask.getDuration().toMinutes();
            if (start == null || subtaskStartTime.isBefore(start)) {
                start = subtaskStartTime;
            }
            if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
            epicNewDuration = epicNewDuration.plusMinutes(subtaskDuration);
        }
        epic.setStartTime(start);
        epic.setEndTime(endTime);
        epic.setDuration(epicNewDuration.toMinutes());
    }
}