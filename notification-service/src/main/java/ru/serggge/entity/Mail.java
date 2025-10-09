package ru.serggge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RedisHash("Mail")
@Entity
@NoArgsConstructor
@Data
public class Mail implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    private String email;
    private String message;
    @Indexed
    @Column(name = "received_at")
    private Instant receivedAt;

    public Mail(String email, String message, Instant receivedAt) {
        this.email = email;
        this.message = message;
        this.receivedAt = receivedAt;
    }
}