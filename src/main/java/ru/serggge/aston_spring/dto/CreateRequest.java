package ru.serggge.aston_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateRequest(@NotBlank String name,
                            @NotNull @Pattern(regexp = "^\\d+@\\d+\\.\\d{2,3}$") String email,
                            @Positive int age) {
}
