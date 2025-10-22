package ru.serggge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.serggge.entity.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByLogin(String login);
}
