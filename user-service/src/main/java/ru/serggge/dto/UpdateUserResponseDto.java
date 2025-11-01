package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponseDto {

    @Schema(description = "Unique entity identifier", example = "1", format = "long")
    private Long id;
    @Schema(description = "User name", example = "John Doe")
    private String name;
    @Schema(description = "Unique user email", example = "john@email.org", format = "email")
    private String email;
    @Schema(description = "User age", example = "25", format = "integer")
    private Integer age;
    @Schema(description = "Event creation time in epoch millis", example = "1323216465", format = "instant")
    private Instant createdAt;
}