package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Board;
import org.example.trello.entity.TaskList;
import org.example.trello.repository.BoardRepository;
import org.example.trello.repository.TaskListRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/list")
public class TaskListWebController {

    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    @PostMapping("/create")
    public String createTaskList(@RequestParam Long boardId,
                                 @RequestParam String title,
                                 Authentication auth) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/board/" + boardId + "?error=unauthorized";
        }

        TaskList taskList = new TaskList();
        taskList.setTitle(title);
        taskList.setBoard(board);
        taskListRepository.save(taskList);

        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{listId}/update")
    public String updateTaskList(@PathVariable Long listId,
                                 @RequestParam String title,
                                 Authentication auth) {
        TaskList taskList = taskListRepository.findById(listId).orElseThrow();
        Long boardId = taskList.getBoard().getId();

        if (!taskList.getBoard().getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/board/" + boardId + "?error=unauthorized";
        }

        taskList.setTitle(title);
        taskListRepository.save(taskList);

        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{listId}/delete")
    public String deleteTaskList(@PathVariable Long listId, Authentication auth) {
        TaskList taskList = taskListRepository.findById(listId).orElseThrow();
        Long boardId = taskList.getBoard().getId();

        if (!taskList.getBoard().getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/board/" + boardId + "?error=unauthorized";
        }

        taskListRepository.delete(taskList);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/create/ajax")
    public String createListAjax(@RequestParam Long boardId,
                                 @RequestParam String title,
                                 Model model) {
        TaskList list = TaskList.builder()
                .title(title)
                .board(boardRepository.findById(boardId).orElseThrow())
                .build();
        taskListRepository.save(list); 

        model.addAttribute("list", list);
        return "fragments/list :: list"; // fragment для одного списка
    }

}