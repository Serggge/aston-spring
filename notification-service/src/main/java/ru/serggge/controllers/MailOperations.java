package ru.serggge.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.SendMailRequestDto;
import ru.serggge.dto.SendMailResponseDto;

@Tag(name = "Notification", description = "Notification API")
@RequestMapping("/mail")
public interface MailOperations {

    @Operation(summary = "Send notification about account activity")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification successfully sent"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request")
    })
    @PostMapping
    SendMailResponseDto sendMail(@RequestBody @Valid SendMailRequestDto requestDto);

}