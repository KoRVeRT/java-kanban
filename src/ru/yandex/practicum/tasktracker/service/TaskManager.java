package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private int generatorId = 1;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        Collection<Task> tasks = this.tasks.values();
        return new ArrayList<>(tasks);
    }

    public ArrayList<Subtask> getAllSubTasks() {
        Collection<Subtask> subTasks = this.subTasks.values();
        return new ArrayList<>(subTasks);
    }

    public ArrayList<Epic> getAllEpics() {
        Collection<Epic> epics = this.epics.values();
        return new ArrayList<>(epics);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()){ // Clear the list of subtasks for each epic.
            epic.getSubtaskIds().clear();
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear(); // If you have removed Epic, then delete its subtasks.
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subTasks.get(subtaskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public void addTask(Task task) {
        task.setId(generatorId);
        tasks.put(generatorId, task);
        generatorId++;
    }

    public void addSubtask(Subtask subtask) {
        Epic currentEpic = epics.get(subtask.getEpicId());
        subtask.setId(generatorId);
        subTasks.put(generatorId, subtask);
        currentEpic.getSubtaskIds().add(generatorId);
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
        generatorId++;
    }

    public void addEpic(Epic epic) {
        epic.setId(generatorId);
        epics.put(generatorId, epic);
        epic.setStatus(TaskStatus.NEW);
        generatorId++;
    }

    public void updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
    }

    public void updateEpic(Epic updateEpic) {
        updateEpic.setStatus(calculateEpicStatus(updateEpic));
        epics.put(updateEpic.getId(), updateEpic);
    }

    public void updateSubtask(Subtask updateSubtask) {
        subTasks.put(updateSubtask.getId(), updateSubtask);
        Epic currentEpic = epics.get(updateSubtask.getEpicId());
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        Epic currentEpic = epics.get(currentSubtask.getEpicId());
        subTasks.remove(subtaskId);
        currentEpic.getSubtaskIds().remove((Integer) subtaskId);
        currentEpic.setStatus(calculateEpicStatus(currentEpic));
    }

    public void deleteEpicById(int id) {
        Epic currentEpic = getEpicById(id);
        for (Integer subtask : currentEpic.getSubtaskIds()) {
            subTasks.remove(subtask); // If you have removed Epic, then delete its subtasks.
        }
        epics.remove(id);
    }

    public ArrayList<Integer> getAllSubtasksOfEpic(int epicId) {
        Epic currentEpic = epics.get(epicId);
        return currentEpic.getSubtaskIds();
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
