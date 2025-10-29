package ru.serggge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import ru.serggge.config.properties.AuthProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class UserServiceSecurityConfig {

    private final AuthProperties authProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .oauth2ResourceServer(configurer ->
                        configurer.jwt(jwt -> jwt
                                .jwtAuthenticationConverter(converter())))
                .oauth2Client(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.DELETE, "/users")
                        .hasRole("ADMIN")
                        .requestMatchers("/actuator/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated());

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri(authProperties.getJwkSetUri())
                .build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> converter() {
        return jwt -> {
            Collection<?> rawAuthorities = (Collection<?>) jwt.getClaims()
                    .getOrDefault("roles", Collections.emptyList());
            Set<SimpleGrantedAuthority> roles = rawAuthorities
                    .stream()
                    .map(Object::toString)
                    .map(authority -> String.join("_", "Role", authority))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toUnmodifiableSet());

            return new JwtAuthenticationToken(jwt, roles);
        };
    }
}