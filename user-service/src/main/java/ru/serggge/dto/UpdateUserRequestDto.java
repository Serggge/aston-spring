package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    @Schema(description = "User name", example = "John Doe")
    private String name;
    @Schema(description = "Unique user email", example = "john@email.org", format = "email")
    @NotNull
    @Email(message = "Email should be valid")
    private String email;
    @Schema(description = "User age", example = "25", format = "integer")
    @Min(0)
    @Max(200)
    private Integer age;
}
