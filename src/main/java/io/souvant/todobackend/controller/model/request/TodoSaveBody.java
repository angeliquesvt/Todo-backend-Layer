package io.souvant.todobackend.controller.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class TodoSaveBody {

    @NotEmpty
    @NotBlank
    @NotNull
    private String title;

    @JsonCreator
    public TodoSaveBody(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
