package ru.serggge.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.serggge.dto.RegisterRequestDto;
import ru.serggge.entity.Account;
import ru.serggge.service.AccountService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequestDto requestDto) {
        Account newAccount = accountMapper.toEntity(requestDto);
        Account registeredAccount = accountService.register(newAccount);
    }
}
