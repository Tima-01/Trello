package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Board;
import org.example.trello.entity.User;
import org.example.trello.repository.BoardRepository;
import org.example.trello.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardWebController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;



    @PostMapping("/boards/create")
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
        model.addAttribute("board", board);
        model.addAttribute("username", auth.getName());
        return "board";
    }
}
