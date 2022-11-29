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
    public void remove(int taskId) {
        removeNode(nodes.get(taskId));
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task);
        if (nodes.isEmpty()) {
            first = newNode;
        } else {
            final Node oldLast = last;
            newNode.previous = oldLast;
            oldLast.next = newNode;
        }
        last = newNode;
        nodes.put(task.getId(), newNode);
    }

    private void removeNode(Node removedNode) {
        if (removedNode == null) {
            return;
        }
        if (removedNode == first && removedNode == last) {
            first = null;
            last = null;
        } else if (removedNode == first) {
            removedNode.next.previous = null;
            first = removedNode.next;
        } else if (removedNode == last) {
            removedNode.previous.next = null;
            last = removedNode.previous;
        } else {
            removedNode.previous.next = removedNode.next;
            removedNode.next.previous = removedNode.previous;
        }
        nodes.remove(removedNode.task.getId());
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