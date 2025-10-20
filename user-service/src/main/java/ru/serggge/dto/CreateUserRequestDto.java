package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

    @Schema(description = "User name", example = "John Doe", nullable = true)
    @NotBlank
    private String name;
    @Schema(description = "Unique user email", example = "john@email.org", format = "email", nullable = true)
    @NotNull
    @Email(message = "Email should be valid")
    private String email;
    @Schema(description = "User age", example = "25", format = "integer", minimum = "0", maximum = "200")
    @Min(0)
    @Max(200)
    private int age;
}