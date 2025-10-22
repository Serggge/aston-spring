package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.entity.Account;
import ru.serggge.entity.Role;
import ru.serggge.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Account register(Account account) {
        account.setRole(Role.ROLE_USER);
        return accountRepository.save(account);
    }
}