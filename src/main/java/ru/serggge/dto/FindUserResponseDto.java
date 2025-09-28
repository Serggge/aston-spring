package ru.serggge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindUserResponseDto {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Instant createdAt;
}