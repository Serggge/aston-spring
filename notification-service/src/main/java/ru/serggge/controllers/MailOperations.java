package ru.serggge.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.SendMailRequestDto;
import ru.serggge.entity.Mail;

@Tag(name = "Notification", description = "Notification API")
@RequestMapping("/mail")
public interface MailOperations {

    @Operation(summary = "Send notification about account activity")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Notification successfully sent"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mail sendMail(@RequestBody @Valid SendMailRequestDto requestDto);

}