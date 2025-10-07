package ru.serggge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMailRequestDto {

    @Email
    @NotNull
    private String email;
    @NotBlank
    private String event;
}
