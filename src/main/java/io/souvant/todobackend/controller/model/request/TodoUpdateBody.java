package io.souvant.todobackend.controller.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class TodoUpdateBody {

    @NotNull
    private String title;

    private Boolean completed;

    @Min(0)
    private Number order;

    @JsonCreator
    public TodoUpdateBody(String title, Boolean completed, Number order) {
        this.title = title;
        this.completed = completed;
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
        TodoUpdateBody that = (TodoUpdateBody) o;
        return Objects.equals(title, that.title) && Objects.equals(completed, that.completed) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, completed, order);
    }
}
