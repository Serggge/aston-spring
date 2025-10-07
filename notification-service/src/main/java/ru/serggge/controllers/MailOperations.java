package ru.serggge.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.FindEmailResponseDto;
import ru.serggge.dto.SendMailRequestDto;
import java.util.List;

@RequestMapping("/mail")
public interface MailOperations {

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendMail(@RequestBody @Valid SendMailRequestDto requestDto);

    @GetMapping
    List<FindEmailResponseDto> getMails(@RequestParam("mail") String email,
                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize);

}