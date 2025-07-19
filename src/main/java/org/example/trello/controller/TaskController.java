package org.example.trello.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "BearerAuth")
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
    public ResponseEntity<String> getTasksByList(@PathVariable Long listId) {
        TaskList list = listRepository.findById(listId).orElseThrow();

        List<Task> tasks = taskRepository.findByList(list);

        if (tasks.isEmpty()) {
            return ResponseEntity.ok("В списке \"" + list.getTitle() + "\" пока нет задач.");
        }

        String result = "Задачи в списке \"" + list.getTitle() + "\":\n" +
                tasks.stream()
                        .map(task -> task.getId() + " - " + task.getTitle() +
                                " (до " + task.getDeadline().toLocalDate() + ")")
                        .reduce((a, b) -> a + "\n" + b)
                        .orElse("");

        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
//        Task task = taskRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
//
//        TaskList list = listRepository.findById(request.getListId())
//                .orElseThrow(() -> new RuntimeException("Список не найден"));
//
//        task.setTitle(request.getTitle());
//        task.setDescription(request.getDescription());
//        task.setDeadline(request.getDeadline());
//        task.setList(list);
//
//        return ResponseEntity.ok(taskRepository.save(task));
//    }


}
