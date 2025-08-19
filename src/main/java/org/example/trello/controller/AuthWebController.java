package org.example.trello.controller;

import lombok.RequiredArgsConstructor;
import org.example.trello.dto.UserRegistrationRequest;
import org.example.trello.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthWebController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute UserRegistrationRequest dto, Model model) {
        userService.register(dto);
        model.addAttribute("message", "Регистрация успешна. Теперь войдите.");
        return "login";
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
}
