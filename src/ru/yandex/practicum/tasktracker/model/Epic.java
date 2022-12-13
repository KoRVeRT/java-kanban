package ru.yandex.practicum.tasktracker.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtaskIds = new HashSet<>();

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public Set<Integer> getSubtaskIds() {
        return Collections.unmodifiableSet(subtaskIds);
    }

    public void addSubtaskId(Integer subtask) {
        subtaskIds.add(subtask);
    }

    public void removeSubtaskId(Integer subtask) {
        subtaskIds.remove(subtask);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toCsvRow() {
        return super.toCsvRow();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Epic) || !super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return subtaskIds.equals(epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}