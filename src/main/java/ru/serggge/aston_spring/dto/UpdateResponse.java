package ru.serggge.aston_spring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateResponse(@NotNull @Positive Long id, String name, String email, Integer age) {
}
