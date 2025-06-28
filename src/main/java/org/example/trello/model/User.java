package org.example.trello.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users") // создастся таблица с названием users
@Data // От Lombok сгенерирует гетеры и сетеры
@NoArgsConstructor // тоже от lombok создаст коструктор пусттой
@AllArgsConstructor // создаст конструктор со всеми полями класса
@Builder // Для понятного чтения
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER) // роли грузятся вместе с юзером
    @CollectionTable( // указывается имя таблицы
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id") // связь между таблицами будет по user_id
    )
    @Column(name = "role")
    private Set<String> roles;

}
