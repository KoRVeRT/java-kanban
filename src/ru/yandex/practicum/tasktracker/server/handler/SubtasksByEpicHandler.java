package ru.yandex.practicum.tasktracker.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SubtasksByEpicHandler implements HttpHandler {
    private final Gson gson = Managers.getGson();
    private final TaskManager taskManager;

    public SubtasksByEpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("The request is processed " + path + " with the method " + method);

        if (method.equals("GET")) {
            String query = httpExchange.getRequestURI().getQuery();
            try {
                int id = Integer.parseInt(query.split("=")[1]);
                statusCode = 200;
                response = gson.toJson(taskManager.getSubtasksByEpicId(id));
            } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                response = "В запросе отсутствует необходимый параметр - id";
            } catch (NumberFormatException e) {
                response = "Неверный формат id";
            }
        } else {
            response = "Некорректный запрос";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}