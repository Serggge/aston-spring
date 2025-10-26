package ru.serggge.service;

import ru.serggge.dto.LoginRequestDto;
import ru.serggge.entity.Account;

public interface AccountService {

    void register(Account account);

    void login(LoginRequestDto requestDto);
}
