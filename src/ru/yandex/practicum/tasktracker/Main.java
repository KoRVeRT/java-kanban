package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.FileBackedTasksManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.utils.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        // create tasks
        Task task1 = new Task();
        task1.setName("Купить батон");
        task1.setDescription("Нужен свежий батон для бутербродов");
        task1.setStatus(TaskStatus.NEW);

        Task task2 = new Task();
        task2.setName("Выбросить мусор");
        task2.setDescription("С этим делом лучше не медлить");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        // create epics
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 6 спринту в Яндекс.Практикуме");

        Epic epic2 = new Epic();
        epic2.setName("Накормить кота");
        epic2.setDescription("Купить корм для кота");
        // add tasks and Epics
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        // create subtasks
        Subtask subtaskSprint1 = new Subtask();
        subtaskSprint1.setName("Закончить тренажер");
        subtaskSprint1.setDescription("Выполнить все задания в тренажере");
        subtaskSprint1.setEpicId(epic1.getId());
        subtaskSprint1.setStatus(TaskStatus.IN_PROGRESS);

        Subtask subtaskSprint2 = new Subtask();
        subtaskSprint2.setName("Посмотреть вебинар");
        subtaskSprint2.setDescription("Итоговый вебинар по ТЗ спринта №6");
        subtaskSprint2.setEpicId(epic1.getId());
        subtaskSprint2.setStatus(TaskStatus.NEW);

        Subtask subtaskSprint3 = new Subtask();
        subtaskSprint3.setName("Сдать ТЗ");
        subtaskSprint3.setDescription("Отправить ТЗ на проверку");
        subtaskSprint3.setEpicId(epic1.getId());
        subtaskSprint3.setStatus(TaskStatus.NEW);
        // add subtasks
        taskManager.addSubtask(subtaskSprint1);
        taskManager.addSubtask(subtaskSprint2);
        taskManager.addSubtask(subtaskSprint3);
        // print
        System.out.println();
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtaskSprint3.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
         // new taskManager2
        System.out.println();
        TaskManager taskManager2 = FileBackedTasksManager.loadFromFile(Managers.pathSaveFile());
        // create task
        Task task3 = new Task();
        task3.setName("Купить колбасу");
        task3.setDescription("Нужна докторская");
        task3.setStatus(TaskStatus.NEW);
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
        taskManager2.addSubtask(subtaskDinner);
        // print
        System.out.println(taskManager2.getTaskById(task3.getId()));
        System.out.println(taskManager2.getEpicById(epic3.getId()));
        System.out.println(taskManager2.getSubtaskById(subtaskDinner.getId()));
        System.out.println(taskManager2.getHistory());
    }
}