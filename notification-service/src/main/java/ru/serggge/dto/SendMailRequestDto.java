package ru.serggge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMailRequestDto {

    @Schema(description = "Unique user email", example = "john@email.org", format = "email", nullable = true)
    @Email
    @NotNull
    private String email;
    @Schema(description = "Event type", example = "create", nullable = true)
    @NotBlank
    private String event;
}
