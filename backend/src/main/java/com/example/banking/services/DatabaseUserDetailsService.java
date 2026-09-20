package com.example.banking.services;

import com.example.banking.models.AppUser;
import com.example.banking.repositories.AppUserRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;

    public DatabaseUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        AppUser user = users.findByEmail(normalized)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Invalid credentials"));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .build();
    }
}
