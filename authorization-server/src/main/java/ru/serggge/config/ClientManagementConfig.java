package ru.serggge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import ru.serggge.config.properties.ClientsProperties;
import java.time.Duration;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ClientManagementConfig {

    private final ClientsProperties clientsProperties;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient gateway = gatewayClient();
        RegisteredClient userService = userServiceClient();
        RegisteredClient mailService = mailServiceClient();

        return new InMemoryRegisteredClientRepository(gateway, userService, mailService);
    }

    private RegisteredClient gatewayClient() {
        return RegisteredClient.withId(UUID.randomUUID()
                                           .toString())
                               .clientId("gateway")
                               .clientSecret(clientsProperties.getGatewaySecret())
                               .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                               .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                               .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                               .redirectUri(clientsProperties.getGatewayRedirectUri())
                               .scope(OidcScopes.OPENID)
                               .clientSettings(ClientSettings.builder()
                                                             .requireProofKey(true)
                                                             .build())
                               .build();
    }

    private RegisteredClient userServiceClient() {
        return RegisteredClient.withId(UUID.randomUUID()
                                           .toString())
                               .clientId("user-service")
                               .clientSecret(clientsProperties.getUserServiceSecret())
                               .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                               .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                               .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                               .tokenSettings(TokenSettings.builder()
                                                           .accessTokenTimeToLive(Duration.ofHours(1))
                                                           .build())
                               .scope(OidcScopes.OPENID)
                               .build();
    }

    private RegisteredClient mailServiceClient() {
        return RegisteredClient.withId(UUID.randomUUID()
                                           .toString())
                               .clientId("mail-service")
                               .clientSecret(clientsProperties.getMailServiceSecret())
                               .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                               .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                               .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                               .tokenSettings(TokenSettings.builder()
                                                           .accessTokenTimeToLive(Duration.ofHours(1))
                                                           .build())
                               .scope(OidcScopes.OPENID)
                               .build();
    }
}