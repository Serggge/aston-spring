package ru.serggge.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Profile("dev")
public class TestController {

    @GetMapping("/info")
    public Authentication check(Authentication authentication) {
        return authentication;
    }

    @GetMapping("/csrf")
    public void csrfOnly() {

    }
}
