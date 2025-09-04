package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.dto.UserRegistrationRequest;
import org.example.trello.entity.Task;
import org.example.trello.entity.TaskList;
import org.example.trello.entity.User;
import org.example.trello.repository.TaskListRepository;
import org.example.trello.repository.TaskRepository;
import org.example.trello.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthWebController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") UserRegistrationRequest dto,
                                 Model model) {
        try {
            userService.register(dto);
            model.addAttribute("message", "Регистрация прошла успешно! Теперь войдите.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("user", dto); 
            return "register";
        }
    }




    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam(required = false) String error,
                              Model model) {
        model.addAttribute("username", username);
        if (error != null) {
            model.addAttribute("message", "Неверное имя пользователя или пароль");
            return "login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("message", "Неверное имя пользователя или пароль");
            return "login";
        }
    }



    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {
        String username = authentication.getName();
        var user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                Authentication authentication,
                                Model model) {
        String username = authentication.getName();
        userService.updateProfile(username, updatedUser);
        model.addAttribute("user", updatedUser);
        model.addAttribute("message", "Профиль успешно обновлен!");
        return "profile";
    }

}