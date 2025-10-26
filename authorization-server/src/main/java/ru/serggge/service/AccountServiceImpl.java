package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.dto.LoginRequestDto;
import ru.serggge.entity.Account;
import ru.serggge.repository.AccountRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(Account account) {
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);
    }

    @Override
    public void login(LoginRequestDto requestDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.login());

        if (userDetails.getPassword().equals(passwordEncoder.encode(requestDto.password()))) {

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Account <{}> successfully logged", userDetails.getUsername());
        } else {
            throw new RuntimeException("Bad credentials");
        }
    }
}