package ru.serggge.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.RegisterRequestDto;
import ru.serggge.entity.Account;
import ru.serggge.service.AccountService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequestDto requestDto) {
        Account newAccount = accountMapper.toEntity(requestDto);
        accountService.register(newAccount);
    }

}