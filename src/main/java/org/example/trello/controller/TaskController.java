package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.dto.TaskRequest;
import org.example.trello.entity.TaskList;
import org.example.trello.entity.Task;
import org.example.trello.repository.TaskListRepository;
import org.example.trello.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskListRepository listRepository;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
        TaskList list = listRepository.findById(request.getListId())
                .orElseThrow(() -> new RuntimeException("Список не найден"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .createdAt(LocalDateTime.now())
                .list(list)
                .build();

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<Task>> getTasksByList(@PathVariable Long listId) {
        TaskList list = listRepository.findById(listId).orElseThrow();
        return ResponseEntity.ok(taskRepository.findByList(list));
    }
}
