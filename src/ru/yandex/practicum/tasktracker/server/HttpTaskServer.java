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
    private HttpServer httpServer;
    private static final int PORT = 8080;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    public void start() throws IOException {
        System.out.println("Running the server on the port " + PORT);
        System.out.println("Open in your browser http://localhost:" + PORT + "/");
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtasksByEpicHandler(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks/", new TasksHandler(taskManager));
        httpServer.start();
    }

    public void stop() {
        System.out.println("Server stopped on port " + PORT);
        httpServer.stop(1);
    }

}