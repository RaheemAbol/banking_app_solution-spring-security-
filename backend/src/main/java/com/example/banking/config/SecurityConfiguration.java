package com.example.banking.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.csrf(Customizer.withDefaults());
        http.requestCache(cache -> cache.disable());
        http.authorizeHttpRequests(auth -> auth
                // Supplied: preserve application errors and support session authentication.
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                // Ticket 1: public information and each signed-in user's account reads.
                .requestMatchers(HttpMethod.GET, "/api/public/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/accounts/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")
                // Ticket 2: money movement belongs to customers.
                .requestMatchers(HttpMethod.POST,
                        "/api/accounts/*/deposits", "/api/accounts/*/withdrawals",
                        "/api/transfers").hasRole("CUSTOMER")
                // Ticket 3: administrators review and freeze/reactivate accounts.
                .requestMatchers(HttpMethod.GET, "/api/admin/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/accounts/*/status").hasRole("ADMIN")
                // Keep this last. Unlisted routes stay inaccessible.
                .anyRequest().denyAll());

        http.formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("email")
                .successHandler((request, response, authentication) ->
                        response.setStatus(200))
                .failureHandler((request, response, exception) ->
                        response.setStatus(401)));

        http.logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) ->
                        response.setStatus(204)));

        http.exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) ->
                        response.setStatus(401))
                .accessDeniedHandler((request, response, exception) ->
                        response.setStatus(403)));
        return http.build();
    }
}
