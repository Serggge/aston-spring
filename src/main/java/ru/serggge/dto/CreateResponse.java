package ru.serggge.dto;

import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateResponse {

    private long id;
    private String name;
    private String email;
    private Integer age;
    private Instant createdAt;

}