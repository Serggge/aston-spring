package ru.serggge.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RedisHash("Mail")
@Data
public class Mail implements Serializable {

    @Id
    private UUID id;
    @Indexed
    private String email;
    private String message;
    private Instant receivedAt;

    public Mail(String email, String message, Instant receivedAt) {
        this.email = email;
        this.message = message;
        this.receivedAt = receivedAt;
    }
}