package ru.yandex.practicum.tasktracker.model;

import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtaskIds = new HashSet<>();

    public HashSet<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", subTaskId=" + subtaskIds +
                '}';
    }
}