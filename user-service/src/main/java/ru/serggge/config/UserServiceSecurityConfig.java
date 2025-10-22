package ru.serggge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import ru.serggge.config.properties.CustomSecurityProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class UserServiceSecurityConfig {

    private final CustomSecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(configurer ->
                configurer.jwt(jwt -> jwt
                        .jwkSetUri(securityProperties.getJwkSetUri())
                        .jwtAuthenticationConverter(converter())));

        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.DELETE, "/users/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated());

        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> converter() {
        return jwt -> {
            Collection<?> rawAuthorities = (Collection<?>) jwt.getClaims()
                                                              .getOrDefault("authorities", Collections.emptyList());
            Set<SimpleGrantedAuthority> grantedAuthorities = rawAuthorities
                    .stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            return new JwtAuthenticationToken(jwt, grantedAuthorities);
        };
    }
}