package io.souvant.todobackend.repository;

import io.souvant.todobackend.repository.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {

    List<TodoEntity> findAllByOrderByOrderAsc();

    @Query(value = "select max(rank) FROM TODO", nativeQuery = true)
    Optional<Integer> getMaxOrder();

    @Modifying
    @Query(value = "delete from TODO where completed = 1", nativeQuery = true)
    void deleteCompletedTodos();

    @Query(value = "SELECT * FROM TODO WHERE rank = :order and id != :id", nativeQuery = true)
    Optional<TodoEntity> findTodoByOrder(@Param("order") Integer order, UUID id);
}
