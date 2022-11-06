package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.utils.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        // create tasks
        Task task1 = new Task();
        task1.setName("Купить батон.");
        task1.setDescription("Главное чтобы он был свежим и длинным");
        task1.setStatus(TaskStatus.NEW);

        Task task2 = new Task();
        task2.setName("Выбросить мусор.");
        task2.setDescription("С этим делом лучше не медлить.");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        // create epics
        Epic epic1 = new Epic();
        epic1.setName("Сделать ТЗ.");
        epic1.setDescription("Итоговое ТЗ по 3 спринту в Яндекс.Практикуме.");

        Epic epic2 = new Epic();
        epic2.setName("Накормить кота.");
        epic2.setDescription("Найти чем накормить кота.");
        // create subtasks
        Subtask subtaskSprint1 = new Subtask();
        subtaskSprint1.setName("Закончить тренажер.");
        subtaskSprint1.setDescription("Выполнить все задания в тренажере.");
        subtaskSprint1.setEpicId(3);
        subtaskSprint1.setStatus(TaskStatus.IN_PROGRESS);

        Subtask subtaskSprint2 = new Subtask();
        subtaskSprint2.setName("Посмотреть вебинар.");
        subtaskSprint2.setDescription("Итоговый вебинар по ТЗ спринта №3.");
        subtaskSprint2.setEpicId(3);
        subtaskSprint2.setStatus(TaskStatus.NEW);

        Subtask subtaskCat1 = new Subtask();
        subtaskCat1.setName("Купить корм.");
        subtaskCat1.setDescription("Нужно именно сходить за ним, доставки нет.");
        subtaskCat1.setEpicId(4);
        subtaskCat1.setStatus(TaskStatus.DONE);
        // add
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtaskSprint1);
        taskManager.addSubtask(subtaskSprint2);
        taskManager.addSubtask(subtaskCat1);
        // print
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        //update
        Subtask updateSubtaskCat1 = new Subtask();
        updateSubtaskCat1.setId(7);
        updateSubtaskCat1.setEpicId(4);
        updateSubtaskCat1.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtaskCat1.setName("Еда для кошек");
        updateSubtaskCat1.setDescription("Купить Whiskas");
        taskManager.updateSubtask(updateSubtaskCat1);

        Subtask updateSubtaskSprint1 = new Subtask();
        updateSubtaskSprint1.setId(5);
        updateSubtaskSprint1.setEpicId(3);
        updateSubtaskSprint1.setStatus(TaskStatus.DONE);
        updateSubtaskSprint1.setName("Решить все задания.");
        updateSubtaskSprint1.setDescription("Нужно закончить все задания в тренажере.");
        taskManager.updateSubtask(updateSubtaskSprint1);

        Subtask updateSubtaskSprint2 = new Subtask();
        updateSubtaskSprint2.setId(6);
        updateSubtaskSprint2.setEpicId(3);
        updateSubtaskSprint2.setStatus(TaskStatus.DONE);
        updateSubtaskSprint2.setName("Скачать вебинар.");
        updateSubtaskSprint2.setDescription("Чтобы посмотреть в поезде оффлайн.");
        taskManager.updateSubtask(updateSubtaskSprint2);

        Task updateTask1 = new Task();
        updateTask1.setName("Нарезать батон.");
        updateTask1.setDescription("Под бутерброды на Новый год.");
        updateTask1.setStatus(TaskStatus.DONE);
        updateTask1.setId(1);
        taskManager.updateTask(updateTask1);

        Task updateTask2 = new Task();
        updateTask2.setName("Сделать уборку.");
        updateTask2.setDescription("Генеральная уборка перед новым годом.");
        updateTask2.setStatus(TaskStatus.NEW);
        updateTask2.setId(2);
        taskManager.updateTask(updateTask2);

        Epic updateEpic1 = new Epic();
        updateEpic1.setName("Сдать на проверку ТЗ.");
        updateEpic1.setDescription("Отправить на проверку итоговое ТЗ №3 в Яндекс.Практикуме.");
        updateEpic1.setId(3);
        updateEpic1.addSubtaskId(5);
        updateEpic1.addSubtaskId(6);
        taskManager.updateEpic(updateEpic1);

        Epic updateEpic2 = new Epic();
        updateEpic2.setName("Пополнить запасы еды для животных.");
        updateEpic2.setDescription("Список что нужно купить для животных.");
        updateEpic2.setId(4);
        updateEpic2.addSubtaskId(7);
        taskManager.updateEpic(updateEpic2);
        // print
        System.out.println();
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getEpicById(4));
        System.out.println(taskManager.getSubtasksByEpicId(3));
        System.out.println(taskManager.getSubtasksByEpicId(4));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getSubtaskById(6));
        System.out.println(taskManager.getSubtaskById(7));
        // print history
        System.out.println();
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        System.out.println(taskManager.getHistory());
        // delete
        taskManager.deleteSubtaskById(5);
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(4);
        // print
        System.out.println();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        // delete allSubtask
        taskManager.deleteAllSubtasks();
        // print Epics
        System.out.println();
        System.out.println(taskManager.getAllEpics());
    }
}