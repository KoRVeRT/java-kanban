package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager, Comparator<Task> {
    protected int generatorId;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
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
        if (task.getStartTime() == null) {
            return true;
        }
        List<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime() == null) {
                break;
            }
            if (task.getStartTime().isAfter(prioritizedTask.getStartTime())
                    && task.getStartTime().isBefore(prioritizedTask.getEndTime())) {
                return false;
            }
        }
        return true;
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
        if (!(checkIntersections(task))) {
            return;
        }
        ++generatorId;
        task.setId(generatorId);
        tasks.put(generatorId, task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!(checkIntersections(subtask))) {
            return;
        }
        ++generatorId;
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(generatorId);
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
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
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
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    protected void calculateEpicParameters(Epic epic) {
        epic.setStatus(calculateEpicStatus(epic));
        epic.setDuration(calculateEpicDuration(epic));
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

    private long calculateEpicDuration(Epic epic) {
        Duration epicNewDuration = Duration.ofMinutes(0);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subTasks.get(subtaskId);
            if (subtask.getStartTime() == null) {
                continue;
            }
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            long subtaskDuration = subtask.getDuration().toMinutes();
            if (epic.getStartTime() == null) {
                epic.setStartTime(subtaskStartTime.format(Task.FORMATTER_OF_DATE));
            } else {
                if (subtaskStartTime.isBefore(epic.getStartTime())) {
                    epic.setStartTime(subtaskStartTime.format(Task.FORMATTER_OF_DATE));
                }
            }
            if (epic.getEndTime() == null) {
                epic.setEndTime(subtaskEndTime);
            } else {
                if (subtaskEndTime.isAfter(epic.getEndTime())) {
                    epic.setEndTime(subtask.getEndTime());
                }
            }
            epicNewDuration = epicNewDuration.plusMinutes(subtaskDuration);
        }
        return epicNewDuration.toMinutes();
    }

    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartTime() == null && o2.getStartTime() != null) {
            return -1;
        } else if (o1.getStartTime() != null && o2.getStartTime() == null) {
            return 1;
        } else if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return 0;
        } else {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    }
}