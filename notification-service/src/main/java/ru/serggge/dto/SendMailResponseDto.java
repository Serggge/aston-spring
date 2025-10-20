package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SendMailResponseDto {

    @Schema(description = "Unique entity identifier in UUID format", example = "f32a64-3b12...", format = "UUID")
    private String id;
    @Schema(description = "Unique user email", example = "john@email.org", format = "email")
    private String email;
    @Schema(description = "Email message body", example = "Hello, mr.John...")
    private String message;
    @Schema(description = "Date and time when event created", example = "2025-01-12 12:00:34", format = "DateTime")
    private LocalDateTime receivedAt;
}
