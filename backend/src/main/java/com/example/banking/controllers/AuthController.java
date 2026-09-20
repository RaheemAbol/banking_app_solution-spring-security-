package com.example.banking.controllers;

import com.example.banking.models.AppUser;
import com.example.banking.repositories.AppUserRepository;
import com.example.banking.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrationService registrationService;
    private final AppUserRepository users;

    public AuthController(RegistrationService registrationService,
            AppUserRepository users) {
        this.registrationService = registrationService;
        this.users = users;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser register(@RequestParam("displayName") String displayName,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        return registrationService.register(displayName, email, password);
    }

    @GetMapping("/me")
    public AppUser me(Authentication authentication) {
        return users.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}
