package ru.serggge.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDto(
        @NotNull @Length(min = 3) String login,
        @NotNull @Length(min = 8) String password) {
}
