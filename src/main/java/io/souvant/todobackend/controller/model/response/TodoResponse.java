package io.souvant.todobackend.controller.model.response;

import java.util.Objects;

public class TodoResponse {
    private final String id;
    private final String title;
    private final Boolean completed;
    private final Number order;
    private String url;

    public TodoResponse(String id, String title, Boolean completed, Integer order, String url) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.order = order;
        this.url = url;
    }

    public String getId() {
        return id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoResponse that = (TodoResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(completed, that.completed) && Objects.equals(order, that.order) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, completed, order, url);
    }
}
