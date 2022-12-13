package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toCsvRow() {
        return super.toCsvRow() + "," + epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Subtask) || !super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}