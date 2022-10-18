package ru.yandex.practicum.tasktracker.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
