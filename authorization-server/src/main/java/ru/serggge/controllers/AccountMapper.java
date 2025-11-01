package ru.serggge.controllers;

import org.springframework.stereotype.Component;
import ru.serggge.dto.LoginRequestDto;
import ru.serggge.dto.RegisterRequestDto;
import ru.serggge.entity.Account;

@Component
public class AccountMapper {

    public Account toEntity(RegisterRequestDto requestDto) {
        return new Account(requestDto.login(), requestDto.password());
    }

    public Account toEntity(LoginRequestDto requestDto) {
        return new Account(requestDto.login(), requestDto.password());
    }
}
