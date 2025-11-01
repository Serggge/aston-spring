package ru.serggge.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/info")
public class AuthInfoController {

    @GetMapping("/hello")
    public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
        return oauth2User
                .map(OAuth2User::getName)
                .map(name -> String.format("Hi, %s", name));
    }

    @GetMapping("/id-token")
    public Mono<OidcIdToken> getIdToken(@AuthenticationPrincipal Mono<OidcUser> oidcUser) {
        return oidcUser.map(OidcUser::getIdToken);
    }

    @GetMapping("/access-token")
    public OAuth2AccessToken getAccessToken(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
        return client.getAccessToken();
    }
}
