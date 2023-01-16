package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.HttpTaskManager;
import ru.yandex.practicum.tasktracker.server.KVServer;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskManager taskManager1 = new HttpTaskManager(Managers.getDefaultHistory(),
                "http://localhost:" + KVServer.PORT);
        // create tasks
        Task task1 = new Task();
        task1.setName("Купить батон");
        task1.setDescription("Нужен свежий батон для бутербродов");
        task1.setStatus(TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 20));
        task1.setDuration(25);

        Task task2 = new Task();
        task2.setName("Купить машину");
        task2.setDescription("");
        task2.setStatus(TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2023, Month.JANUARY, 8, 12, 20));
        task2.setDuration(25);
        // create epics
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 6 спринту в Яндекс.Практикуме");
        // add tasks and Epics
        taskManager1.addEpic(epic1);
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);
        // create subtasks
        Subtask subtask1 = new Subtask();
        subtask1.setName("Закончить тренажер");
        subtask1.setDescription("Выполнить все задания в тренажере");
        subtask1.setEpicId(epic1.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 21, 20));
        subtask1.setDuration(120);
        Subtask subtask2 = new Subtask();
        subtask2.setName("Посмотреть вебинар");
        subtask2.setDescription("Итоговый вебинар по ТЗ спринта №6");
        subtask2.setEpicId(epic1.getId());
        subtask2.setStatus(TaskStatus.NEW);
        subtask2.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 23, 55));
        subtask2.setDuration(122);
        // add subtasks
        taskManager1.addSubtask(subtask2);
        taskManager1.addSubtask(subtask1);
        System.out.println(taskManager1.getAllEpics());
        HttpTaskManager taskManager2 = new HttpTaskManager(Managers.getDefaultHistory(),
                "http://localhost:" + KVServer.PORT);
        System.out.println(taskManager2.getAllTasks());
        taskManager2.loadFromServer();
        System.out.println(taskManager2.getAllTasks());
        System.out.println(taskManager2.getPrioritizedTasks());
    }
}