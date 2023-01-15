package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.server.handler.EpicHandler;
import ru.yandex.practicum.tasktracker.server.handler.HistoryHandler;
import ru.yandex.practicum.tasktracker.server.handler.SubtasksByEpicHandler;
import ru.yandex.practicum.tasktracker.server.handler.SubtaskHandler;
import ru.yandex.practicum.tasktracker.server.handler.TaskHandler;
import ru.yandex.practicum.tasktracker.server.handler.TasksHandler;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtasksByEpicHandler(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks/", new TasksHandler(taskManager));
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Остановлен сервер на порту " + PORT);
        httpServer.stop(1);
    }

}