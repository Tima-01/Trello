package org.example.trello.dto;

import lombok.Data;

@Data
public class TaskListRequest {
    private String title;
    private Integer position;
}

