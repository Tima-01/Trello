package org.example.trello.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.trello.dto.BoardRequest;
import org.example.trello.entity.Board;
import org.example.trello.entity.User;
import org.example.trello.repository.BoardRepository;
import org.example.trello.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class BoardController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequest request, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Board board = Board.builder()
                .title(request.getTitle())
                .owner(user)
                .build();
        System.out.println("Auth user: " + auth.getName());
        return ResponseEntity.ok(boardRepository.save(board));
    }

    @GetMapping
    public ResponseEntity<String> getMyBoards(Authentication auth) {
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<Board> boards = boardRepository.findByOwnerOrMembersContains(currentUser, currentUser);

        if (boards.isEmpty()) {
            return ResponseEntity.ok("У тебя пока нет досок.");
        }

        String boardInfo = boards.stream()
                .map(board -> board.getId() + " - " + board.getTitle())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        String message = "Твои доски: " + boardInfo;

        return ResponseEntity.ok(message);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardRequest request,
            Authentication auth) {

        Board board = boardRepository.findById(id).orElseThrow();



        if (!board.getOwner().getUsername().equals(auth.getName())) {
            System.out.println("403 ERROR: Пользователь не владелец доски");
            return ResponseEntity.status(403).build();
        }

        board.setTitle(request.getTitle());
        return ResponseEntity.ok(boardRepository.save(board));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            Authentication auth) {

        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        boardRepository.delete(board);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{boardId}/members/{username}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long boardId,
            @PathVariable String username,
            Authentication auth) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        // Только владелец доски может добавлять участников
        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        User userToAdd = userRepository.findByUsername(username).orElseThrow();

        // Чтобы не было дубликатов
        if (!board.getMembers().contains(userToAdd)) {
            board.getMembers().add(userToAdd);
            boardRepository.save(board);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}/members/{username}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long boardId,
            @PathVariable String username,
            Authentication auth) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        // Только владелец может удалять участников
        if (!board.getOwner().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        User userToRemove = userRepository.findByUsername(username).orElseThrow();

        if (board.getMembers().contains(userToRemove)) {
            board.getMembers().remove(userToRemove);
            boardRepository.save(board);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{boardId}/members")
    public ResponseEntity<String> getBoardMembers(
            @PathVariable Long boardId,
            Authentication auth) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        boolean isOwner = board.getOwner().getUsername().equals(auth.getName());
        boolean isMember = board.getMembers().stream()
                .anyMatch(user -> user.getUsername().equals(auth.getName()));

        if (!isOwner && !isMember) {
            return ResponseEntity.status(403).build();
        }

        // Собираем строку с именами всех участников
        String members = board.getMembers().stream()
                .map(User::getUsername)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Нет участников");

        String message = "Участники доски: " + members;

        return ResponseEntity.ok(message);
    }

}
