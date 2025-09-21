package ru.serggge.aston_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "USERS")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    private String name;
    private String email;
    private Integer age;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public User(@NonNull String name, @NonNull String email, @NonNull Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
