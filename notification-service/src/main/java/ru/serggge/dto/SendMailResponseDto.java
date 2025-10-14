package ru.serggge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SendMailResponseDto {

    private String id;
    private String email;
    private String message;
    private LocalDateTime receivedAt;
}
