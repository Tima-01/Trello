package org.example.trello.repository;

import org.example.trello.entity.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByBoardId(Long boardId);
    List<TaskList> findByBoardIdOrderByIdAsc(Long boardId);
}
