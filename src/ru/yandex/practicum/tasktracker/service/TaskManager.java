package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;

public interface TaskManager {


    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    HistoryManager getHistoryManager();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int taskId);

    void deleteSubtaskById(int subtaskId);

    void deleteEpicById(int id);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

}