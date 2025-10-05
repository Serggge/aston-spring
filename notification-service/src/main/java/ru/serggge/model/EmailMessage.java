package ru.serggge.model;

import java.time.Instant;

public record EmailMessage(String email, Event event, Instant createdAt) {
}
