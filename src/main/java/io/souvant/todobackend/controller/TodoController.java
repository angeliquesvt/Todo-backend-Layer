package io.souvant.todobackend.controller;

import io.souvant.todobackend.controller.model.request.TodoPartialUpdateBody;
import io.souvant.todobackend.controller.model.request.TodoSaveBody;
import io.souvant.todobackend.controller.model.request.TodoUpdateBody;
import io.souvant.todobackend.controller.model.response.TodoResponse;
import io.souvant.todobackend.service.TodoService;
import io.souvant.todobackend.service.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
@CrossOrigin
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(@RequestBody @Valid TodoSaveBody todoSaveBody) {
        Todo todoSaved = todoService.createTodo(todoSaveBody.getTitle());
        return generateTodoResponse(todoSaved);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoResponse> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return todos.stream().map(TodoController::generateTodoResponse).collect(Collectors.toList());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodos(@RequestParam(required = false) Optional<Boolean> completed) { // Todo: mettre boolean
        todoService.deleteTodos(completed);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResponse getTodo(@PathVariable UUID id) {
        Todo todo = todoService.getTodo(id);
        return generateTodoResponse(todo);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResponse updateTodo(@PathVariable UUID id, @RequestBody @Valid TodoUpdateBody todoUpdateBody) {
        Todo todo = todoService.updateTodo(id, todoUpdateBody);
        return generateTodoResponse(todo);
    }


    /*
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResponse patchTodo(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        Todo todoUpdated = todoService.patchTodo(id, fieldsToUpdate);
        return generateTodoResponse(todoUpdated);
    } */

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResponse patchTodo(@PathVariable UUID id, @RequestBody @Valid TodoPartialUpdateBody todoPartialUpdateBody) {
        Todo todoUpdated = todoService.patchTodo(id, todoPartialUpdateBody);
        return generateTodoResponse(todoUpdated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable UUID id) {
        todoService.deleteTodo(id);
    }

    private static String formatTodoGetUrl(Todo todoSaved) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("todos", "{id}")
                .buildAndExpand(todoSaved.getId())
                .toUriString();
    }

    private static TodoResponse generateTodoResponse(Todo todo) {
        return new TodoResponse(todo.getId().toString(), todo.getTitle(), todo.getCompleted(), todo.getOrder(), formatTodoGetUrl(todo));
    }
}
