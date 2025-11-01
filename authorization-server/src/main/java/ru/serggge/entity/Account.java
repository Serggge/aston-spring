package ru.serggge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "accounts")
@DynamicInsert
@Getter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 64, unique = true, nullable = false)
    private String login;
    @Column(length = 2000, nullable = false)
    @Setter
    private String password;
    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;
    private boolean blocked;

    public Account(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
