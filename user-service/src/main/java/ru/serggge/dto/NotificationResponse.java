package ru.serggge.dto;

import java.time.LocalDateTime;

public record NotificationResponse(String id, String email, String message, LocalDateTime receivedAt) {
}
