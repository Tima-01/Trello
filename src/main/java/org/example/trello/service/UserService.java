package org.example.trello.service;

import lombok.RequiredArgsConstructor;
import org.example.trello.dto.UserRegistrationRequest;
import org.example.trello.entity.User;
import org.example.trello.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User register(UserRegistrationRequest dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Пользователь с таким никнеймом уже существует");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Эта почта уже используется");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .roles(Set.of("ROLE_USER"))
                .build();

        return userRepository.save(user);
    }
}