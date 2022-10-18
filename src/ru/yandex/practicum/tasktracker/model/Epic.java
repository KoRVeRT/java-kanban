package ru.yandex.practicum.tasktracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", subTaskId=" + subTaskId +
                '}';
    }
}




