package ru.serggge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDto(
        @NotBlank String login,
        @NotNull @Length(min = 8) String password) {
}
