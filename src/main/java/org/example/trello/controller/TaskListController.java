package org.example.trello.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.trello.dto.TaskListRequest;
import org.example.trello.entity.Board;
import org.example.trello.entity.TaskList;
import org.example.trello.repository.BoardRepository;

import org.example.trello.repository.TaskListRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TaskListController {

    private final TaskListRepository listRepository;
    private final BoardRepository boardRepository;

    @PostMapping("/{boardId}")
    public ResponseEntity<TaskList> createList(
            @PathVariable Long boardId,
            @RequestBody TaskListRequest request,
            Authentication auth) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        // Только участник или владелец
        boolean allowed = board.getOwner().getUsername().equals(auth.getName()) ||
                board.getMembers().stream().anyMatch(u -> u.getUsername().equals(auth.getName()));
        if (!allowed) return ResponseEntity.status(403).build();

        TaskList list = TaskList.builder()
                .title(request.getTitle())
                .position(request.getPosition())
                .board(board)
                .build();

        return ResponseEntity.ok(listRepository.save(list));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<String> getLists(@PathVariable Long boardId, Authentication auth) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        boolean allowed = board.getOwner().getUsername().equals(auth.getName()) ||
                board.getMembers().stream().anyMatch(u -> u.getUsername().equals(auth.getName()));

        if (!allowed) return ResponseEntity.status(403).build();

        List<TaskList> lists = listRepository.findByBoardIdOrderByPosition(boardId);

        if (lists.isEmpty()) {
            return ResponseEntity.ok("На этой доске пока нет списков.");
        }

        String result = "Списки доски:\n" + lists.stream()
                .map(list -> list.getId() + " - " + list.getTitle())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");

        return ResponseEntity.ok(result);
    }

}
