package ru.yandex.practicum.tasktracker.service.exception;

public class IntersectionException extends RuntimeException {
    public IntersectionException() {
        super();
    }

    public IntersectionException(String message) {
        super(message);
    }

    public IntersectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
