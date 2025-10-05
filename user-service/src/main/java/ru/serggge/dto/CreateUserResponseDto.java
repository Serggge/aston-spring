package ru.serggge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponseDto {

    private long id;
    private String name;
    private String email;
    private Integer age;
    private Instant createdAt;

}