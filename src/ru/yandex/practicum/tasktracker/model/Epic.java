package ru.yandex.practicum.tasktracker.model;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtaskIds = new HashSet<>();

    public Set<Integer> getSubtaskIds() {
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