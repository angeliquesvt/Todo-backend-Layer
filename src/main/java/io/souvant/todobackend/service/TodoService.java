package io.souvant.todobackend.service;

import io.souvant.todobackend.controller.model.request.TodoPartialUpdateBody;
import io.souvant.todobackend.controller.model.request.TodoUpdateBody;
import io.souvant.todobackend.repository.TodoRepository;
import io.souvant.todobackend.repository.entity.TodoEntity;
import io.souvant.todobackend.service.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(String title) {
        Integer todoOrder = generateTodoOrder();
        TodoEntity todoEntity = todoRepository.save(new TodoEntity(title, false, todoOrder));
        return new Todo(todoEntity.getId(), todoEntity.getTitle(), todoEntity.isCompleted(), todoEntity.getOrder());
    }

    public List<Todo> getAllTodos() {
        Iterable<TodoEntity> todoEntities = todoRepository.findAllByOrderByOrderAsc();
        return StreamSupport.stream(todoEntities.spliterator(), false).map(TodoService::mapTodoEntityToTodo).collect(Collectors.toList());
    }
    public void deleteTodos(Optional<Boolean> isCompleted) {
        if (isCompleted.isPresent() && isCompleted.get()) {
            todoRepository.deleteCompletedTodos();
        } else {
            todoRepository.deleteAll();
        }
    }
    public Todo getTodo(UUID id) {
        TodoEntity todoEntityGetById = todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapTodoEntityToTodo(todoEntityGetById);
    }
    public Todo updateTodo(UUID id, TodoUpdateBody todoUpdateBody) { // todo ; faire avec exist
        TodoEntity todoEntityToSave = new TodoEntity(id, todoUpdateBody.getTitle(), todoUpdateBody.getCompleted(), todoUpdateBody.getOrder().intValue());
        todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        todoRepository.findTodoByOrder(todoEntityToSave.getOrder(), id).ifPresent(todoEntity -> {
             throw new ResponseStatusException(HttpStatus.CONFLICT);
        });
        return mapTodoEntityToTodo(todoRepository.save(todoEntityToSave));
    }

    public Todo patchTodo(UUID id, TodoPartialUpdateBody todoRequestBody) {
        TodoEntity todoEntityToUpdate = todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(todoRequestBody.getOrder() != null) {
            todoRepository.findTodoByOrder(todoRequestBody.getOrder().intValue(), id).ifPresent( todoEntity -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            });
        }

        TodoEntity todoEntityUpdatedToSave = mergeTodoEntitieAndTodoRequest(todoEntityToUpdate, todoRequestBody);

        return mapTodoEntityToTodo(todoRepository.save(todoEntityUpdatedToSave));
    }

    private TodoEntity mergeTodoEntitieAndTodoRequest(TodoEntity todoEntityToUpdate, TodoPartialUpdateBody todoRequestBody) {

        if(todoRequestBody.getTitle() != null) {
            if(todoRequestBody.getTitle().equals("" )|| todoRequestBody.getTitle().trim() == "") {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else {
                todoEntityToUpdate.setTitle(todoRequestBody.getTitle());
            }
        }

        if(todoRequestBody.getCompleted() != null) {
            todoEntityToUpdate.setCompleted(todoRequestBody.getCompleted());
        }

        if(todoRequestBody.getOrder() != null) {
            if(todoRequestBody.getOrder().intValue() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            todoEntityToUpdate.setOrder(todoRequestBody.getOrder().intValue());
        }
        return todoEntityToUpdate;
    }


    public void deleteTodo(UUID id) {
        if(!todoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        todoRepository.deleteById(id);
    }

    // utils functions
    private Integer generateTodoOrder() {
        Optional<Integer> todoLastOrder = todoRepository.getMaxOrder();
        return todoLastOrder.isEmpty() ? 1 : todoLastOrder.get() + 1;
    }

    private static Todo mapTodoEntityToTodo(TodoEntity todoEntity) {
        return new Todo(todoEntity.getId(), todoEntity.getTitle(), todoEntity.getCompleted(), todoEntity.getOrder());
    }
}
