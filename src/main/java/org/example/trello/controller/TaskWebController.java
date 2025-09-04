package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Task;
import org.example.trello.entity.TaskList;
import org.example.trello.repository.TaskListRepository;
import org.example.trello.repository.TaskRepository;
import org.example.trello.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.trello.dto.MoveTaskRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskWebController {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    // Создание новой карточки
    @PostMapping("/create")
    public String createTask(
            @RequestParam Long listId,
            @RequestParam Long boardId,
            @RequestParam String title,
            Authentication auth) {

        TaskList taskList = taskListRepository.findById(listId).orElseThrow();

        Task task = Task.builder()
                .title(title)
                .list(taskList)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);
        return "redirect:/board/" + boardId;
    }


    // Обновление карточки
    @PostMapping("/update")
    public String updateTask(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDateTime deadline,
            @RequestParam Long boardId,
            Authentication auth) {

        Task task = taskRepository.findById(id).orElseThrow();

        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);

        taskRepository.save(task);
        return "redirect:/board/" + boardId;
    }

    // Удаление карточки
    @PostMapping("/{id}/delete")
    public String deleteTask(
            @PathVariable Long id,
            @RequestParam Long boardId,
            Authentication auth) {

        taskRepository.deleteById(id);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{id}/move")
    @ResponseBody
    public ResponseEntity<?> moveTask(@PathVariable Long id,
                                      @RequestBody Map<String, Long> body) {
        System.out.println("📥 moveTask: taskId=" + id + ", body=" + body);

        Long newListId = body.get("listId");
        if (newListId == null) {
            return ResponseEntity.badRequest().body("listId is missing");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        TaskList newList = taskListRepository.findById(newListId)
                .orElseThrow(() -> new RuntimeException("List not found: " + newListId));

        System.out.println("Перемещаем таск " + id + " в список " + newListId);

        task.setList(newList);
        taskRepository.save(task);

        return ResponseEntity.ok("Task moved to list " + newListId);
    }

    @PostMapping("/create/ajax")
    public String createTaskAjax(@RequestParam Long listId,
                                 @RequestParam Long boardId,
                                 @RequestParam String title,
                                 Model model) {
        TaskList taskList = taskListRepository.findById(listId).orElseThrow();

        Task task = Task.builder()
                .title(title)
                .list(taskList)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);

        model.addAttribute("task", task);
        return "fragments/card :: card"; // fragment, который рендерит одну карточку
    }


}