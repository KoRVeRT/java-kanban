package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.server.HttpTaskServer;
import ru.yandex.practicum.tasktracker.utils.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static HttpTaskServer taskServer;

    private static final Gson GSON = Managers.getGson();
    private static final String TASK_BASE_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_BASE_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_BASE_URL = "http://localhost:8080/tasks/subtask/";
    private static final String TASKS_BASE_URL = "http://localhost:8080/tasks/";
    private static final String SUBTASKS_OF_EPIC_BASE_URL = "http://localhost:8080/tasks/subtask/epic/";
    private static final String HISTORY_BASE_URL = "http://localhost:8080/tasks/history/";

    @BeforeEach
    void startServer() throws IOException {
        TaskManager manager = Managers.getFileBackedTasksManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        getSubtaskById();
        getTaskById();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(HISTORY_BASE_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(2, arrayTasks.size());
    }

    @Test
    void getSubtasksByEpicId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");
        if (postResponse.statusCode() == 201) {
            int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(epicId);
            Subtask subtask1 = createSubtask("01.01.2022-15:25", 75);
            Subtask subtask2 = createSubtask("01.01.2022-23:25", 75);
            url = URI.create(SUBTASK_BASE_URL);
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask1)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask2)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create(SUBTASKS_OF_EPIC_BASE_URL + "?id=" + epicId);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(2, arrayTasks.size());
        }
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task1 = createTask("01.01.2022-12:20", 45);
        Task task2 = createTask("01.01.2022-18:20", 15);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task1)))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task2)))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create(TASKS_BASE_URL);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(2, arrayTasks.size());
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = createTask("01.01.2022-12:20", 15);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(1, arrayTasks.size());
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(1, arrayTasks.size());
    }

    @Test
    void getAllSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();
        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");

        if (postResponse.statusCode() == 201) {
            int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(epicId);
            Subtask subtask = createSubtask("01.01.2022-15:25", 75);
            url = URI.create(SUBTASK_BASE_URL);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        }
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = createTask("01.01.2022-12:20", 15);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();
        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");

        if (postResponse.statusCode() == 201) {
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            task.setId(id);
            url = URI.create(TASK_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Task responseTask = GSON.fromJson(response.body(), Task.class);
            assertEquals(task, responseTask);
        }
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();
        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");

        if (postResponse.statusCode() == 201) {
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(id);
            url = URI.create(EPIC_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Epic responseTask = GSON.fromJson(response.body(), Epic.class);
            assertEquals(epic, responseTask);
        }
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();
        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");

        if (postResponse.statusCode() == 201) {
            int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(epicId);
            Subtask subtask = createSubtask(
                    "01.01.2022-15:25",
                    75);
            url = URI.create(SUBTASK_BASE_URL);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask)))
                    .build();
            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                subtask.setId(id);
                url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Subtask responseTask = GSON.fromJson(response.body(), Subtask.class);
                assertEquals(subtask, responseTask);
            }
        }
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = createTask("01.01.2022-12:20", 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (postResponse.statusCode() == 201) {
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            Task taskUpdate = createTask("01.01.2022-15:20", 25);
            taskUpdate.setId(id);
            taskUpdate.setStatus(TaskStatus.IN_PROGRESS);
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(taskUpdate)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create(TASK_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            Task responseTask = GSON.fromJson(response.body(), Task.class);
            assertEquals(taskUpdate, responseTask);
        }
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (postResponse.statusCode() == 201) {
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            Epic epicUpdate = createEpic(null, null, List.of());
            epicUpdate.setId(id);
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epicUpdate)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create(EPIC_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Epic responseTask = GSON.fromJson(response.body(), Epic.class);
            assertEquals(epicUpdate, responseTask);
        }
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");
        if (postResponse.statusCode() == 201) {
            int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(epicId);
            Subtask subtask = createSubtask(
                    "01.01.2022-15:25",
                    75);
            url = URI.create(SUBTASK_BASE_URL);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask)))
                    .build();
            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                Subtask subtaskUpdate = createSubtask(
                        "01.01.2022-15:25",
                        65);
                subtaskUpdate.setId(id);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtaskUpdate)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Subtask responseTask = GSON.fromJson(response.body(), Subtask.class);
                assertEquals(subtaskUpdate, responseTask);
            }
        }
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = createTask("01.01.2022-12:20", 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(0, arrayTasks.size());
    }

    @Test
    void deleteEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(0, arrayTasks.size());
    }

    @Test
    void deleteSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();


        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");
        if (postResponse.statusCode() == 201) {
            int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
            epic.setId(epicId);
            Subtask subtask = createSubtask(
                    "01.01.2022-15:25",
                    75);
            url = URI.create(SUBTASK_BASE_URL);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        }
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = createTask("01.01.2022-12:20", 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(task)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id = Integer.parseInt(postResponse.body().split("=")[1]);
        url = URI.create(TASK_BASE_URL + "?id=" + id);
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача с данным id не найдена", response.body());

    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");
        if (postResponse.statusCode() == 201) {
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            url = URI.create(EPIC_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Эпик с данным id не найден", response.body());
        }
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = createEpic("01.01.2022-15:25", "01.01.2022-17:05", List.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(epic)))
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode(), "POST запрос");
        if (postResponse.statusCode() == 201) {
            Subtask subtask = createSubtask(
                    "01.01.2022-15:25",
                    75);
            url = URI.create(SUBTASK_BASE_URL);

            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(subtask)))
                    .build();
            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                subtask.setId(id);
                url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Подзадача с данным id не найдена", response.body());
            }
        }
    }

    private Task createTask(String startTime, long duration) {
        Task task = new Task();
        task.setName("Task");
        task.setStatus(TaskStatus.NEW);
        if (startTime != null) {
            task.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            task.setDuration(duration);
        }
        return task;
    }

    private Epic createEpic(String startTime, String endTime, List<Integer> subtaskId) {
        Epic epic = new Epic();
        epic.setName("Epic");
        epic.setStatus(TaskStatus.NEW);
        if (startTime != null) {
            epic.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (endTime != null) {
            epic.setEndTime(LocalDateTime.parse(endTime, Task.FORMATTER_OF_DATE));
        }
        for (Integer number : subtaskId) {
            epic.addSubtaskId(number);
        }
        return epic;
    }

    private Subtask createSubtask(String startTime, long duration) {
        Subtask subtask = new Subtask();
        subtask.setName("Subtask");
        subtask.setEpicId(1);
        subtask.setStatus(TaskStatus.NEW);
        if (startTime != null) {
            subtask.setStartTime(LocalDateTime.parse(startTime, Task.FORMATTER_OF_DATE));
        }
        if (duration != 0) {
            subtask.setDuration(duration);
        }
        return subtask;
    }
}
