package ru.yandex.practicum.tasktracker.model;

import java.util.*;

public class CustomLinkedList<T extends Task> {
    private final Map<Integer, Node<T>> linkedHashMap = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;

    private static class Node<T> {
        public T task;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T task, Node<T> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    public void linkLast(T task) {
        final Node<T> newNode = new Node<>(null, task, null);
        removeNode(newNode);
        if (linkedHashMap.isEmpty()) {
            tail = newNode;
            head = newNode;
        } else {
            final Node<T> oldTail = tail;
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }
    }

    public List<T> getTasks() {
        List<T> listHistory = new ArrayList<>();
        for (Map.Entry<Integer, Node<T>> entry : linkedHashMap.entrySet()) {
            listHistory.add(entry.getValue().task);
        }
        return listHistory;
    }

    private void removeNode(Node<T> node) {
        linkedHashMap.remove(node.task.getId());
    }
}