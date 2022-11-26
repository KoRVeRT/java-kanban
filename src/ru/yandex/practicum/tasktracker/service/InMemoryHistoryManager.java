package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Node;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> viewHistory = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        viewHistory.linkLast(task);
    }

    @Override
    public void remove(int id) {
        viewHistory.removeNode(viewHistory.nodesMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory.getTasks();
    }

    private static class CustomLinkedList<T extends Task> {
        private final Map<Integer, Node<T>> nodesMap = new HashMap<>();
        private Node<T> head;
        private Node<T> tail;

        private void linkLast(T task) {
            final Node<T> newNode = new Node<>(null, task, null);
            removeNode(nodesMap.get(task.getId()));
            if (nodesMap.isEmpty()) {
                tail = newNode;
                head = newNode;
            } else {
                final Node<T> oldTail = tail;
                tail = newNode;
                newNode.setPrev(oldTail);
                oldTail.setNext(newNode);
            }
            nodesMap.put(task.getId(), newNode);
        }

        private List<T> getTasks() {
            List<T> listHistory = new ArrayList<>();
            Node<T> currentNode = head;
            while (currentNode != null) {
                listHistory.add(currentNode.getTask());
                currentNode = currentNode.getNext();
            }
            return listHistory;
        }

        private void removeNode(Node<T> node) {
            if (node != null) {
                if (node.getPrev() != null && node.getNext() != null) {
                    node.getPrev().setNext(node.getNext());
                    node.getNext().setPrev(node.getPrev());
                } else if (node.getNext() == null && node.getPrev() != null) {
                    node.getPrev().setNext(null);
                    tail = node.getPrev();
                } else if (node.getNext() != null) {
                    node.getNext().setPrev(null);
                    head = node.getNext();
                } else {
                    head = null;
                    tail = null;
                }
                nodesMap.remove(node.getTask().getId());
            }
        }
    }
}