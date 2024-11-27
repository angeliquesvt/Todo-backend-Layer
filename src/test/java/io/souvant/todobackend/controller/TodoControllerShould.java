package io.souvant.todobackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.souvant.todobackend.controller.model.request.TodoPartialUpdateBody;
import io.souvant.todobackend.controller.model.request.TodoSaveBody;
import io.souvant.todobackend.controller.model.request.TodoUpdateBody;
import io.souvant.todobackend.controller.model.response.TodoResponse;
import io.souvant.todobackend.service.TodoService;
import io.souvant.todobackend.service.model.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class TodoControllerShould {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TodoService todoService;

    @Test
    @DisplayName("call createTodo service and generate a TodoResponse")
    void createTodo() throws Exception {
        // GIVEN
        String title = "my test todo";
        UUID uuid = UUID.randomUUID();
        TodoSaveBody todoSaveBody = new TodoSaveBody(title);
        Todo todo = new Todo(uuid, title, false, 1);
        TodoResponse expectedResponse = new TodoResponse(uuid.toString(), title, false, 1, "http://localhost/todos/"+uuid);

        when(todoService.createTodo(title)).thenReturn(todo);

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/todos")
                                .content(objectMapper.writeValueAsString(todoSaveBody))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @DisplayName("get all todos and generate list of TodoResponse")
    void getAllTodos() throws Exception {
        // GIVEN
        String title = "my test todo";
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        Todo todo1 = new Todo(uuid, "title1", false, 1);
        Todo todo2 = new Todo(uuid2, "title2", true, 2);
        Todo todo3 = new Todo(uuid3, "title3", false, 3);

        List<Todo> todos = new ArrayList<>();
        todos.add(todo1);
        todos.add(todo2);
        todos.add(todo3);

        when(todoService.getAllTodos()).thenReturn(todos);

        TodoResponse todoResponse1 = new TodoResponse(todo1.getId().toString(), todo1.getTitle(), todo1.getCompleted(), todo1.getOrder(), "http://localhost/todos/"+uuid);
        TodoResponse todoResponse2 = new TodoResponse(todo2.getId().toString(), todo2.getTitle(), todo2.getCompleted(), todo2.getOrder(), "http://localhost/todos/"+uuid2);
        TodoResponse todoResponse3 = new TodoResponse(todo3.getId().toString(), todo3.getTitle(), todo3.getCompleted(), todo3.getOrder(), "http://localhost/todos/"+uuid3);

        List<TodoResponse> expectedResponses = new ArrayList<>();
        expectedResponses.add(todoResponse1);
        expectedResponses.add(todoResponse2);
        expectedResponses.add(todoResponse3);

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponses)));
    }

    @Test
    @DisplayName("call deleteTodos service")
    void deleteTodos() throws Exception {
        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodos(any());
    }

    @Test
    @DisplayName("get call todoService to get a todo by id and return TodoResponse")
    void getTodo() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        Todo todo = new Todo(uuid, "test", false, 1);
        TodoResponse expectedResponse = new TodoResponse(uuid.toString(), "test", false, 1, "http://localhost/todos/"+uuid);

        when(todoService.getTodo(uuid)).thenReturn(todo);

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/todos/" + uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(todoService, times(1)).getTodo(uuid);
    }

    @Test
    @DisplayName("update todo")
    void updateTodo() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoUpdateBody todoUpdateBody = new TodoUpdateBody("test", true, 2);
        Todo todo = new Todo(uuid, "test", true, 2);
        TodoResponse expectedResponse = new TodoResponse(uuid.toString(), "test", true, 2, "http://localhost/todos/"+uuid);

        when(todoService.updateTodo(uuid, todoUpdateBody)).thenReturn(todo);

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/todos/" + uuid)
                                .content(objectMapper.writeValueAsString(todoUpdateBody))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(todoService, times(1)).updateTodo(uuid, todoUpdateBody);
    }

    @Test
    @DisplayName("call patchTodo service and generate an updated TodoResponse")
    void patchTodo() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody("test", null, null);
        Todo todoResponse = new Todo(uuid, "test", true, 2);
        TodoResponse expectedResponse = new TodoResponse(uuid.toString(), "test", true, 2, "http://localhost/todos/"+uuid);

        when(todoService.patchTodo(uuid, requestBody)).thenReturn(todoResponse);

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/todos/" + uuid)
                                .content(objectMapper.writeValueAsString(requestBody))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));


        verify(todoService, times(1)).patchTodo(uuid, requestBody);
    }

    @Test
    @DisplayName("call delete todo service")
    void deleteTodo() throws Exception {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // WHEN
        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/todos/" + uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo(uuid);
    }
}



