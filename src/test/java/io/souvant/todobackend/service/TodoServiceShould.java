package io.souvant.todobackend.service;

import io.souvant.todobackend.controller.model.request.TodoPartialUpdateBody;
import io.souvant.todobackend.controller.model.request.TodoUpdateBody;
import io.souvant.todobackend.repository.TodoRepository;
import io.souvant.todobackend.repository.entity.TodoEntity;
import io.souvant.todobackend.service.model.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceShould {

    @Mock
    TodoRepository todoRepository;

    @InjectMocks
    TodoService todoService;

    @Test
    @DisplayName("generate a todo rank save it and retrn a Todo")
    void createTodo() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoEntity todoEntity = new TodoEntity("test", false, 1);
        todoEntity.setId(uuid);

        when(todoRepository.getMaxOrder()).thenReturn(Optional.of(1));
        when(todoRepository.save(any())).thenReturn(todoEntity);

        Todo expectedResult = new Todo(uuid, "test", false, 1);

        // WHEN
        Todo result = todoService.createTodo("title");

        // THEN
        verify(todoRepository, times(1)).save(any());
        //todo : verify
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("return get and return a list of Todo")
    void getAllTodos() {
        // GIVEN
        String title = "my test todo";
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        TodoEntity todo1 = new TodoEntity("title1", false, 1);
        TodoEntity todo2 = new TodoEntity("title2", true, 2);
        TodoEntity todo3 = new TodoEntity("title3", false, 3);
        todo1.setId(uuid);
        todo2.setId(uuid2);
        todo3.setId(uuid3);

        List<TodoEntity> todoEntities = new ArrayList<>();
        todoEntities.add(todo1);
        todoEntities.add(todo2);
        todoEntities.add(todo3);

        when(todoRepository.findAllByOrderByOrderAsc()).thenReturn(todoEntities);

        Todo todoResponse1 = new Todo(todo1.getId(), todo1.getTitle(), todo1.getCompleted(), todo1.getOrder());
        Todo todoResponse2 = new Todo(todo2.getId(), todo2.getTitle(), todo2.getCompleted(), todo2.getOrder());
        Todo todoResponse3 = new Todo(todo3.getId(), todo3.getTitle(), todo3.getCompleted(), todo3.getOrder());

        List<Todo> expectedResponses = new ArrayList<>();
        expectedResponses.add(todoResponse1);
        expectedResponses.add(todoResponse2);
        expectedResponses.add(todoResponse3);

        // WHEN
        List<Todo> result = todoService.getAllTodos();

        // THEN
        verify(todoRepository, times(1)).findAllByOrderByOrderAsc();
        assertEquals(expectedResponses, result);
    }

    @Test
    @DisplayName("delete completed todos")
    void deleteCompletedTodos() {
        // WHEN
        todoService.deleteTodos(Optional.of(true));

        // THEN
        verify(todoRepository, times(1)).deleteCompletedTodos();
    }

    @Test
    @DisplayName("delete all todos")
    void deleteTodos() {
        // WHEN
        todoService.deleteTodos(Optional.empty());

        // THEN
        verify(todoRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("get a todo by id")
    void getTodo() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoEntity todo1 = new TodoEntity("title1", false, 1);
        todo1.setId(uuid);
        Todo expectedResult = new Todo(todo1.getId(), todo1.getTitle(), todo1.isCompleted(), todo1.getOrder());

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todo1));

        // WHEN
        Todo result = todoService.getTodo(uuid);

        // THEN
        verify(todoRepository, times(1)).findById(uuid);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("throw not found Response status when todo not found")
    void getTodoThrowNotFound() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoEntity todo1 = new TodoEntity("title1", false, 1);
        todo1.setId(uuid);

        when(todoRepository.findById(uuid)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.getTodo(uuid));

        // THEN
        assertEquals(HttpStatus.NOT_FOUND,thrown.getStatus());
        verify(todoRepository, times(1)).findById(uuid);
    }

    @Test
    @DisplayName("return an updated Todo")
    void updateTodo() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoUpdateBody todoUpdateBody = new TodoUpdateBody("test", true, 2);
        TodoEntity todoFoundById = new TodoEntity(uuid, "title1", false, 2);
        Todo expectedResult = new Todo(uuid, todoUpdateBody.getTitle(), todoUpdateBody.getCompleted(), todoUpdateBody.getOrder().intValue());
        TodoEntity todoEntitySaved = new TodoEntity(uuid, "test", true, 2);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.findTodoByOrder(todoUpdateBody.getOrder().intValue(), uuid)).thenReturn(Optional.empty());
        when(todoRepository.save(any())).thenReturn(todoEntitySaved);

        InOrder inOrder = inOrder(todoRepository);

        // WHEN
         Todo result = todoService.updateTodo(uuid, todoUpdateBody);

        // THEN
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(1)).findTodoByOrder(2, uuid);
        inOrder.verify(todoRepository, times(1)).save(any());
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("throw a httpStatus NOT FOUND when Todo is not found by id for update")
    void notUpdateTodoAndThrowNotFoundException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoUpdateBody todoUpdateBody = new TodoUpdateBody("test", true, 2);
        TodoEntity todoEntityToSave = new TodoEntity(uuid, "test", true, 2);
        when(todoRepository.findById(uuid)).thenReturn(Optional.empty());
        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.updateTodo(uuid, todoUpdateBody));

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(0)).findTodoByOrder(2, uuid);
        inOrder.verify(todoRepository, times(0)).save(todoEntityToSave);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Should throw a httpStatus CONFLICT when the order to update is already exist")
    void notUpdateTodoAndThrowConflictException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoUpdateBody todoUpdateBody = new TodoUpdateBody("test", true, 2);
        TodoEntity todoEntityToSave = new TodoEntity(uuid, "test", true, 2);
        TodoEntity todoFoundById = new TodoEntity(uuid, "title1", false, 1);
        TodoEntity todoEntityFoundByOrder = new TodoEntity(uuid, "title4", true, 2);
        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.findTodoByOrder(2, uuid)).thenReturn(Optional.of(todoEntityFoundByOrder));
        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.updateTodo(uuid, todoUpdateBody));

        // THEN
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(1)).findTodoByOrder(2, uuid);
        inOrder.verify(todoRepository, times(0)).save(todoEntityToSave);
    }
    @Test
    @DisplayName("return a partially updated Todo")
    void partiallyUpdateTodo() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody("Title changed", true, 2);
        TodoEntity todoFoundById = new TodoEntity(uuid, "Title to change", false, 1);
        Todo expectedResult = new Todo(uuid, "Title changed", true, 2);
        TodoEntity todoEntitySaved = new TodoEntity(uuid, "Title changed", true, 2);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.findTodoByOrder(2, uuid)).thenReturn(Optional.empty());
        when(todoRepository.save(todoEntitySaved)).thenReturn(todoEntitySaved);

        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        Todo result = todoService.patchTodo(uuid, requestBody);

        // THEN
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(1)).findTodoByOrder(2, uuid);
        inOrder.verify(todoRepository, times(1)).save(todoEntitySaved);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("update title")
    void updateTitle() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody("Title changed", null, null);
        TodoEntity todoFoundById = new TodoEntity(uuid, "Title to change", false, 1);
        Todo expectedResult = new Todo(uuid, "Title changed", false, 1);
        TodoEntity todoEntitySaved = new TodoEntity(uuid, "Title changed", false, 1);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.save(todoEntitySaved)).thenReturn(todoEntitySaved);

        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        Todo result = todoService.patchTodo(uuid, requestBody);

        // THEN
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(0)).findTodoByOrder(1, uuid);
        inOrder.verify(todoRepository, times(1)).save(todoEntitySaved);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("update completed")
    void updateCompleted() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody(null, true, null);
        TodoEntity todoFoundById = new TodoEntity(uuid, "Title", false, 1);
        Todo expectedResult = new Todo(uuid, "Title", true, 1);
        TodoEntity todoEntitySaved = new TodoEntity(uuid, "Title", true, 1);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.save(todoEntitySaved)).thenReturn(todoEntitySaved);

        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        Todo result = todoService.patchTodo(uuid, requestBody);

        // THEN
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(0)).findTodoByOrder(1, uuid);
        inOrder.verify(todoRepository, times(1)).save(todoEntitySaved);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("update order")
    void updateOrder() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody(null, null, 2);
        TodoEntity todoFoundById = new TodoEntity(uuid, "Title", true, 1);
        Todo expectedResult = new Todo(uuid, "Title", true, 2);
        TodoEntity todoEntitySaved = new TodoEntity(uuid, "Title", true, 2);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.save(todoEntitySaved)).thenReturn(todoEntitySaved);

        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        Todo result = todoService.patchTodo(uuid, requestBody);

        // THEN
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(1)).findTodoByOrder(2, uuid);
        inOrder.verify(todoRepository, times(1)).save(todoEntitySaved);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("throw a httpStatus NOT FOUND when Todo is not found by id for partial update")
    void notPartiallyUpdateTodoAndThrowNotFoundException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody("test", true, 1);


        when(todoRepository.findById(uuid)).thenReturn(Optional.empty());
        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.patchTodo(uuid, requestBody));

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(0)).findTodoByOrder(1, uuid);
        inOrder.verify(todoRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw a httpStatus CONFLICT when the order to update is already exist")
    void notPartiallyUpdateTodoAndThrowConflictWhenOrderAlreadyExist() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        TodoPartialUpdateBody requestBody = new TodoPartialUpdateBody("test", true, 1);


        TodoEntity todoFoundById = new TodoEntity(uuid, "test", false, 1);
        TodoEntity todoEntityToUpdateUpdated = new TodoEntity(uuid, "save Gotham", true, 1);
        TodoEntity todoEntityFoundByOrder = new TodoEntity(uuid, "title4", true,  1);

        when(todoRepository.findById(uuid)).thenReturn(Optional.of(todoFoundById));
        when(todoRepository.findTodoByOrder(1,  uuid)).thenReturn(Optional.of(todoEntityFoundByOrder));
        InOrder inOrder = inOrder(todoRepository);

        // WHEN
        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.patchTodo(uuid, requestBody));

        // THEN
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
        inOrder.verify(todoRepository, times(1)).findById(uuid);
        inOrder.verify(todoRepository, times(1)).findTodoByOrder(1, uuid);
        inOrder.verify(todoRepository, times(0)).save(todoEntityToUpdateUpdated);
    }

    @Test
    @DisplayName("Delete an existing todo by id")
    void deleteTodoById() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(todoRepository.existsById(uuid)).thenReturn(true);

        // WHEN
        todoService.deleteTodo(uuid);

        // THEN
        verify(todoRepository, times(1)).deleteById(uuid);
    }

    @Test
    @DisplayName("Should throw not found exception when Todo isn't find by id")
    void deleteTodoThrowNotFoundException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(todoRepository.existsById(uuid)).thenReturn(false);

        // WHEN

        ResponseStatusException thrown = assertThrows( ResponseStatusException.class, () ->
                todoService.deleteTodo(uuid));


        // THEN
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        verify(todoRepository, times(0)).deleteById(uuid);
        verifyNoMoreInteractions(todoRepository);
    }
}