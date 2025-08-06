package org.example.trello.controller;


import lombok.RequiredArgsConstructor;
import org.example.trello.entity.Board;
import org.example.trello.entity.User;
import org.example.trello.repository.BoardRepository;
import org.example.trello.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Board> allBoards = boardRepository.findAll();
        List<Board> userBoards = allBoards.stream()
                .filter(board ->
                        board.getOwner().getUsername().equals(username) ||
                                board.getMembers().stream().anyMatch(m -> m.getUsername().equals(username))
                )
                .collect(Collectors.toList());

        model.addAttribute("username", username);
        model.addAttribute("boards", userBoards);
        return "home";
    }


}

