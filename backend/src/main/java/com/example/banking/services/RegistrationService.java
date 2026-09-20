package com.example.banking.services;

import com.example.banking.models.AppUser;
import com.example.banking.models.BankAccount;
import com.example.banking.repositories.BankAccountRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.banking.repositories.AppUserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationService {
    private static final Logger log =
            LoggerFactory.getLogger(RegistrationService.class);
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final BankAccountRepository accounts;

    public RegistrationService(AppUserRepository users,
            PasswordEncoder passwordEncoder, BankAccountRepository accounts) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.accounts = accounts;
    }

    @Transactional
    public AppUser register(String displayName, String email, String password) {
        String name = displayName.trim();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.length() > 100
                || normalizedEmail.length() > 150
                || !normalizedEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")
                || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide a name, email and password of 8 or more characters"
                    + " (at most 72 UTF-8 bytes for BCrypt).");
        }
        if (users.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already registered.");
        }
        String hash = passwordEncoder.encode(password);
        AppUser user = new AppUser(name, normalizedEmail, hash, "CUSTOMER");
        try {
            AppUser saved = users.saveAndFlush(user);
            accounts.save(new BankAccount(saved, "CHECKING"));
            accounts.save(new BankAccount(saved, "SAVINGS"));
            log.info("Registered customer id={}", saved.getId());
            return saved;
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Registration conflicts with an existing record.");
        }
    }
}
