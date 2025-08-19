package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Task;
import org.example.trello.entity.TaskList;
import org.example.trello.repository.TaskListRepository;
import org.example.trello.repository.TaskRepository;
import org.example.trello.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.trello.dto.MoveTaskRequest;
import java.time.LocalDateTime;

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
    @PostMapping("/{taskId}/move")
    @ResponseBody
    public void moveTask(@PathVariable Long taskId, @RequestBody MoveTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        TaskList newList = taskListRepository.findById(request.getListId()).orElseThrow();

        task.setList(newList);
        taskRepository.save(task);
    }
}