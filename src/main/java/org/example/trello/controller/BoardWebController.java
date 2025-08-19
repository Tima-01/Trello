package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Board;
import org.example.trello.entity.TaskList;
import org.example.trello.entity.User;
import org.example.trello.repository.BoardRepository;
import org.example.trello.repository.TaskRepository;
import org.example.trello.repository.UserRepository;
import org.example.trello.repository.TaskListRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardWebController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;

    @PostMapping("/create")
    public String createBoard(@RequestParam String title, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Board board = new Board();
        board.setTitle(title);
        board.setOwner(user);
        boardRepository.save(board);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String openBoard(@PathVariable Long id, Model model, Authentication auth) {
        Board board = boardRepository.findById(id).orElseThrow();
        List<TaskList> taskLists = taskListRepository.findByBoardIdOrderByIdAsc(id); // Загружаем списки задач
        taskLists.forEach(list ->
                list.setTasks(taskRepository.findByList(list)));
        model.addAttribute("board", board);
        model.addAttribute("taskLists", taskLists); // Добавляем списки в модель
        model.addAttribute("username", auth.getName());
        return "board";
    }

    @PostMapping("/{id}/update")
    public String updateBoard(@PathVariable Long id,
                              @RequestParam String title,
                              Authentication auth) {
        Board board = boardRepository.findById(id).orElseThrow();

        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/board/" + id + "?error=unauthorized";
        }

        board.setTitle(title);
        boardRepository.save(board);
        return "redirect:/board/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, Authentication auth) {
        Board board = boardRepository.findById(id).orElseThrow();

        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return "redirect:/board/" + id + "?error=unauthorized";
        }

        boardRepository.delete(board);
        return "redirect:/";
    }
}