package ru.serggge.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotBlank
    private String name;
    @NotNull
    @Email(message = "Email should be valid")
    private String email;
    @Min(0)
    @Max(200)
    private int age;
}