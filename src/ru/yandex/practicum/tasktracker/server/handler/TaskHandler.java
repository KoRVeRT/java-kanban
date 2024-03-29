package ru.yandex.practicum.tasktracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {
    private final Gson gson = Managers.getGson();
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("The request is processed " + path + " with the method " + method);

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllTasks());
                    System.out.println("GET TASKS: " + jsonString);
                    response = jsonString;
                } else {
                    try {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            response = gson.toJson(task);
                        } else {
                            response = "Задача с данным id не найдена";
                        }
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    Integer id = task.getId();
                    if (id != null) {
                        taskManager.updateTask(task);
                        System.out.println("UPDATE TASK: " + task);
                        statusCode = 200;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        taskManager.addTask(task);
                        System.out.println("CREATED TASK: " + task);
                        int idCreated = task.getId();
                        statusCode = 201;
                        response = "Создана задача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException | NumberFormatException e) {
                    statusCode = 400;
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllTasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.split("=")[1]);
                        taskManager.deleteTaskById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = 400;
                response = "Некорректный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}