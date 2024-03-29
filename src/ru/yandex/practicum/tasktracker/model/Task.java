package ru.yandex.practicum.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public static final DateTimeFormatter FORMATTER_OF_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm");

    private Integer id;
    private String name;
    private TaskStatus status;
    private String description;
    protected LocalDateTime startTime;
    protected Duration duration = Duration.ofMinutes(0);


    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toCsvRow() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                id, getType(), name, status, description,
                startTime,
                duration.toMinutes(),
                getEndTime());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long minutes) {
        duration = Duration.ofMinutes(minutes);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + (startTime != null ? startTime.format(Task.FORMATTER_OF_DATE) : null) +
                ", duration=" + duration.toMinutes() + "min" +
                ", endTime=" + (getEndTime() != null ? getEndTime().format(FORMATTER_OF_DATE) : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && status == task.status
                && Objects.equals(id, task.id)
                && Objects.equals(description, task.description)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status, id, description, startTime, duration);
    }
}