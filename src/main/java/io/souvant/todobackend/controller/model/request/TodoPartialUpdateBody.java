package io.souvant.todobackend.controller.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Min;
import java.util.Objects;

public class TodoPartialUpdateBody {

    private String title;
    private Boolean completed;

    @Min(0)
    private Number order;

    @JsonCreator
    public TodoPartialUpdateBody(String title, Boolean completed, Number order) {
        this.title = title;
        this.completed = completed;
        this.order = order;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setOrder(Number order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Number getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoPartialUpdateBody that = (TodoPartialUpdateBody) o;
        return Objects.equals(title, that.title) && Objects.equals(completed, that.completed) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, completed, order);
    }
}
