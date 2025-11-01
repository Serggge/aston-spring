package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NotificationResponse(
        @Schema(description = "Unique entity identifier", example = "1", format = "long") String id,
        @Schema(description = "Unique user email", example = "john@email.org", format = "email") String email,
        @Schema(description = "Email message body", example = "Hello, Mr.Doe...") String message,
        @Schema(description = "Receive message time", example = "2025-01-14...", format = "DateTime") LocalDateTime receivedAt) {
}
