package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int generatorId = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getAllSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subTasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear(); // If you have removed Epic, then delete its subtasks.

    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public Epic getEpicById(int subEpicId) {
        return epics.get(subEpicId);
    }

    public void addTask(Task task, TaskStatus status) {
        task.setId(generatorId);
        task.setStatus(status);
        tasks.put(generatorId, task);
        generatorId++;
    }

    public void addSubtaskToEpic(Subtask subtask, TaskStatus status, int epicId) {
        Epic currentEpic = epics.get(epicId);
        if (currentEpic != null) {
            subtask.setId(generatorId);
            subtask.setStatus(status);
            subtask.setEpicId(epicId);
            subTasks.put(generatorId, subtask);
            currentEpic.getSubTaskId().add(generatorId);
            TaskStatus newStatusOfEpic = calculationEpicStatus(currentEpic);
            currentEpic.setStatus(newStatusOfEpic);
            generatorId++;
        } else {
            System.out.println("Epic = отсутствует");
        }
    }

    public void addEpic(Epic epic) {
        epic.setId(generatorId);
        epics.put(generatorId, epic);
        epic.setStatus(TaskStatus.NEW);
        generatorId++;
    }

    public void updateTaskName(int taskId, String newName) {
        Task currentTask = tasks.get(taskId);
        if (currentTask != null) {
            currentTask.setName(newName);
        } else {
            System.out.println("Задача = отсутствует");
        }
    }

    public void updateTaskDescription(int taskId, String newDescription) {
        Task currentTask = tasks.get(taskId);
        if (currentTask != null) {
            currentTask.setDescription(newDescription);
        } else {
            System.out.println("Задача = отсутствует");
        }
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus) {
        Task currentTask = tasks.get(taskId);
        if (currentTask != null) {
            currentTask.setStatus(newStatus);
        } else {
            System.out.println("Задача = отсутствует");
        }
    }

    public void updateEpicName(int epicId, String newName) {
        Epic currentEpic = epics.get(epicId);
        if (currentEpic != null) {
            currentEpic.setName(newName);
        } else {
            System.out.println("Epic = отсутствует");
        }
    }

    public void updateEpicDescription(int epicId, String newDescription) {
        Epic currentEpic = epics.get(epicId);
        if (currentEpic != null) {
            currentEpic.setDescription(newDescription);
        } else {
            System.out.println("Epic = отсутствует");
        }
    }

    public void updateSubtaskName(int subtaskId, String newName) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        if (currentSubtask != null) {
            currentSubtask.setName(newName);
        } else {
            System.out.println("Подзадача = отсутствует");
        }
    }

    public void updateSubtaskDescription(int subtaskId, String newDescription) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        if (currentSubtask != null) {
            currentSubtask.setDescription(newDescription);
        } else {
            System.out.println("Подзадача = отсутствует");
        }
    }

    public void updateSubtaskStatus(int subtaskId, TaskStatus newStatus) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        if (currentSubtask != null) {
            currentSubtask.setStatus(newStatus);
            Epic currentEpic = epics.get(currentSubtask.getEpicId());
            TaskStatus newStatusOfEpic = calculationEpicStatus(currentEpic);
            currentEpic.setStatus(newStatusOfEpic);
        } else {
            System.out.println("Подзадача = отсутствует");
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask currentSubtask = subTasks.get(subtaskId);
        if (currentSubtask != null) {
            Epic currentEpic = epics.get(currentSubtask.getEpicId());
            subTasks.remove(subtaskId);
            currentEpic.getSubTaskId().remove((Integer) subtaskId);
            TaskStatus newStatusOfEpic = calculationEpicStatus(currentEpic);
            currentEpic.setStatus(newStatusOfEpic);
        } else {
            System.out.println("Подзадача = отсутствует");
        }
    }

    public void deleteEpicById(int id) {
        Epic currentEpic = getEpicById(id);
        for (Integer subtask : currentEpic.getSubTaskId()) {
            subTasks.remove(subtask); // If you have removed Epic, then delete its subtasks.
        }
        epics.remove(id);
    }

    public ArrayList<Integer> getAllSubtasksOfEpic(int epicId) {
        Epic currentEpic = epics.get(epicId);
        if (currentEpic != null) {
            return currentEpic.getSubTaskId();
        } else {
            System.out.println("Epic = отсутствует");
            return null;
        }
    }

    private TaskStatus calculationEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;

        for (Integer subtaskId : epic.getSubTaskId()) {
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
