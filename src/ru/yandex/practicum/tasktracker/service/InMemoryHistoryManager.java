package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodes = new HashMap<>();

    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> listHistory = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            listHistory.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return listHistory;
    }

    @Override
    public void remove(int taskId) {
        removeNode(nodes.get(taskId));
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task);
        removeNode(nodes.get(task.getId()));
        if (nodes.isEmpty()) {
            first = newNode;
            last = newNode;
        } else {
            final Node oldLast = last;
            last = newNode;
            newNode.previous = oldLast;
            oldLast.next = newNode;
        }
        nodes.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.previous != null && node.next != null) {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        } else if (node.next == null && node.previous != null) {
            node.previous.next = null;
            last = node.previous;
        } else if (node.next != null) {
            node.next.previous = null;
            first = node.next;
        } else {
            first = null;
            last = null;
        }
        nodes.remove(node.task.getId());

    }

    private static class Node {
        Task task;
        Node previous;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }
}