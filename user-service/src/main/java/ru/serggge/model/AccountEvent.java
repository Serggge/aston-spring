package ru.serggge.model;

public record AccountEvent(String email, String event, Long createdAt) {
}
