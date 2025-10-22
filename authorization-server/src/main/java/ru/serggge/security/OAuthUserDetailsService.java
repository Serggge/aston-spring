package ru.serggge.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.serggge.entity.Account;
import ru.serggge.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class OAuthUserDetailsService implements UserDetailsService {

    private final AccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account user = userRepository.findByLogin(username)
                                     .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getLogin())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
