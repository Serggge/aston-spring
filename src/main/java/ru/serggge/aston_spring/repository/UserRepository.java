package ru.serggge.aston_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.serggge.aston_spring.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
