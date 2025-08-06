package org.example.trello.repository;

import org.example.trello.entity.Board;
import org.example.trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwnerOrMembersContains(User owner, User member);

}
