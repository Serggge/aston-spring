package ru.serggge.aston_spring.dto;

public record UpdateRequest(Long id, String name, String email, Integer age) {
}
