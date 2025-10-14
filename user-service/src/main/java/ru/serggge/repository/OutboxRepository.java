package ru.serggge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.serggge.entity.OutboxEvent;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
            SELECT id, event, email, created_at
            FROM outbox_event AS e
            FOR UPDATE
            SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findAllNonBlocked();

}