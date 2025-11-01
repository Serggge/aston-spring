package ru.serggge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.*;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http,
                                            ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver,
                                            ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
                                            ServerLogoutSuccessHandler logoutSuccessHandler,
                                            ServerLogoutHandler logoutHandler) {
        http
                .authorizeExchange(
                        authorizeExchange -> authorizeExchange
                                .pathMatchers(
                                        "/auth/register",
                                        "/auth/signin",
                                        "/actuator/**")
                                .permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.authorizationRequestResolver(authorizationRequestResolver)
                                   .authorizedClientRepository(authorizedClientRepository)
                )
                .logout(logout ->
                        logout.logoutSuccessHandler(logoutSuccessHandler)
                              .logoutHandler(logoutHandler)
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver requestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    public ServerLogoutHandler logoutHandler() {
        return new DelegatingServerLogoutHandler(
                new SecurityContextServerLogoutHandler(),
                new WebSessionServerLogoutHandler(),
                new HeaderWriterServerLogoutHandler(
                        new ClearSiteDataServerHttpHeadersWriter(
                                ClearSiteDataServerHttpHeadersWriter.Directive.COOKIES)
                )
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}