package io.souvant.todobackend.repository.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "TODO")
public class TodoEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String title;

    private Boolean completed;

    @Column(name = "rank")
    private Integer order;

    public TodoEntity() {
    }

    public TodoEntity(String title, Boolean isCompleted, Integer order) {
        this.title = title;
        this.completed = isCompleted;
        this.order = order;
    }

    public TodoEntity(UUID id, String title, Boolean isCompleted, Integer order) {
        this.id = id;
        this.title = title;
        this.completed = isCompleted;
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public Integer getOrder() {
        return order;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoEntity that = (TodoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(completed, that.completed) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, completed, order);
    }
}
