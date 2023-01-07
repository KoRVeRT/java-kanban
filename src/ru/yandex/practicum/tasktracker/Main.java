package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        // create tasks
        Task task1 = new Task();
        task1.setName("Купить батон");
        task1.setDescription("Нужен свежий батон для бутербродов");
        task1.setStatus(TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 20));
        task1.setDuration(25);

        Task task2 = new Task();
        task2.setName("Выбросить мусор");
        task2.setDescription("С этим делом лучше не медлить");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 13, 25));
        task2.setDuration(15);
        // create epics
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 6 спринту в Яндекс.Практикуме");
        // add tasks and Epics
        taskManager.addEpic(epic1);
        taskManager.addTask(task2);
        taskManager.addTask(task1);

        // update task
        Task task4 = new Task();
        task4.setId(task1.getId());
        task4.setName("Спать");
        task4.setDescription("Срочно!");
        task4.setStatus(TaskStatus.IN_PROGRESS);
        task4.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 30));
        task4.setDuration(25);
        taskManager.updateTask(task4);
        // create subtasks
        Subtask subtaskSprint1 = new Subtask();
        subtaskSprint1.setName("Закончить тренажер");
        subtaskSprint1.setDescription("Выполнить все задания в тренажере");
        subtaskSprint1.setEpicId(epic1.getId());
        subtaskSprint1.setStatus(TaskStatus.IN_PROGRESS);
        subtaskSprint1.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 21, 20));
        subtaskSprint1.setDuration(120);

        Subtask subtaskSprint2 = new Subtask();
        subtaskSprint2.setName("Посмотреть вебинар");
        subtaskSprint2.setDescription("Итоговый вебинар по ТЗ спринта №6");
        subtaskSprint2.setEpicId(epic1.getId());
        subtaskSprint2.setStatus(TaskStatus.NEW);
        subtaskSprint2.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 1, 23, 55));
        subtaskSprint2.setDuration(122);
        // add subtasks
        taskManager.addSubtask(subtaskSprint2);
        taskManager.addSubtask(subtaskSprint1);
        // print
        System.out.println();
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getPrioritizedTasks());
        // new taskManager2
        System.out.println();
        TaskManager taskManager2 = Managers.loadFromFile();
        // create task
        Task task3 = new Task();
        task3.setName("Купить колбасу");
        task3.setDescription("Нужна докторская");
        task3.setStatus(TaskStatus.NEW);
        task3.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 5, 12, 20));
        task3.setDuration(25);
        taskManager2.addTask(task3);
        // create epic
        Epic epic3 = new Epic();
        epic3.setName("Приготовить ужин.");
        epic3.setDescription("Ужин на двоих");
        taskManager2.addEpic(epic3);
        // create subtask
        Subtask subtaskDinner = new Subtask();
        subtaskDinner.setName("Сходить за продуктами");
        subtaskDinner.setDescription("Купить продукты для приготовления");
        subtaskDinner.setEpicId(epic3.getId());
        subtaskDinner.setStatus(TaskStatus.IN_PROGRESS);
        subtaskDinner.setStartTime(LocalDateTime.of(2022, Month.JANUARY, 5, 18, 20));
        subtaskDinner.setDuration(45);
        taskManager2.addSubtask(subtaskDinner);
        // print
        System.out.println(taskManager2.getHistory());
        System.out.println(taskManager2.getPrioritizedTasks());
    }
}