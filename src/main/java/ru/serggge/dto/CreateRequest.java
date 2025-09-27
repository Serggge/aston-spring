package ru.serggge.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequest {

    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
    @PositiveOrZero
    @Max(200)
    private int age;
}