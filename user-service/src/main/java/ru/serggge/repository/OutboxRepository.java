package ru.serggge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.serggge.entity.OutboxEvent;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("""
            SELECT e
            FROM OutboxEvent e
            WHERE e.isActive = true
            ORDER BY e.createdAt
            """)
    List<OutboxEvent> findUnsentEvents();

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.isActive = false
            WHERE e.id = :id
            """)
    void markAsCompleted(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.isActive = false
            WHERE e.id IN :ids
            """)
    void markAsCompleted(@Param("ids") Iterable<Long> ids);
}
