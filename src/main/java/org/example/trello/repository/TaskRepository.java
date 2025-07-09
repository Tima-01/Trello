package org.example.trello.repository;

import org.example.trello.entity.Task;
import org.example.trello.entity.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByList(TaskList list);
}
