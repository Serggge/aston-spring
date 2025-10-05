package ru.serggge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.serggge.entity.Credentials;
import java.util.List;
import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

    Optional<Credentials> findByUsername(String username);

    @Query("""
            SELECT c
            FROM Credentials c
            WHERE c.role LIKE 'ROLE_ADMIN'
            """)
    List<Credentials> findAdmins();
}