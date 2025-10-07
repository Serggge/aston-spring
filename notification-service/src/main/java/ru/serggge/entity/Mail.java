package ru.serggge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "mails")
@NoArgsConstructor
@Getter
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 64, nullable = false)
    private String email;
    @Column(length = 2000, nullable = false)
    private String message;
    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;
    @Column(name = "delivered", insertable = false)
    @Setter
    private boolean isDelivered;

    public Mail(String email, String message, Instant receivedAt) {
        this.email = email;
        this.message = message;
        this.receivedAt = receivedAt;
    }
}
