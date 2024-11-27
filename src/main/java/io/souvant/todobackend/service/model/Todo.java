package io.souvant.todobackend.service.model;

import java.util.Objects;
import java.util.UUID;

public class Todo {

    private UUID id;

    private String title;

    private Integer order;

    private Boolean completed;

    public Todo(UUID id, String title, Boolean completed, Integer order) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Todo)) {
            return false;
        }

        Todo todo = (Todo) o;

        return todo.getId() == id && todo.getTitle() == title && todo.completed == completed && todo.getOrder() == order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getOrder() {
        return order;
    }

    public Boolean getCompleted() {
        return completed;
    }
}
