package org.example.trello.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<String> getMe(Authentication auth) {
        return ResponseEntity.ok("Привет, " + auth.getName());
    }
}
