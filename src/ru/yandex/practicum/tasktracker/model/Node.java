package ru.yandex.practicum.tasktracker.model;

public class Node<T> {
    private final T task;
    private Node<T> next;
    private Node<T> prev;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public T getTask() {
        return task;
    }
}
