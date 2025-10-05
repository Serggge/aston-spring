package ru.serggge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.serggge.model.Event;
import java.time.Instant;

@Entity
@Table(name = "outbox_event")
@NoArgsConstructor
@Getter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Event event;
    private String email;
    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "active", insertable = false)
    private boolean isActive;

    public OutboxEvent(Event event, String email) {
        this.event = event;
        this.email = email;
    }
}
