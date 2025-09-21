package ru.serggge.dto;

public record UpdateRequest(Long id, String name, String email, Integer age) {
}
