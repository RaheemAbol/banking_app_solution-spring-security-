package com.example.banking.controllers;

import com.example.banking.models.BankAccount;
import com.example.banking.models.BankTransaction;
import com.example.banking.services.BankingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankingService banking;
    public BankAccountController(BankingService banking) { this.banking = banking; }

    @GetMapping
    public List<BankAccount> list(Authentication auth) {
        return banking.myAccounts(auth.getName());
    }
    @GetMapping("/{id}")
    public BankAccount get(@PathVariable Long id, Authentication auth) {
        return banking.myAccount(id, auth.getName());
    }
    @GetMapping("/{id}/transactions")
    public List<BankTransaction> history(@PathVariable Long id, Authentication auth) {
        return banking.history(id, auth.getName());
    }
    @PostMapping("/{id}/deposits")
    public BankAccount deposit(@PathVariable Long id, Authentication auth,
            @RequestParam BigDecimal amount, @RequestParam(defaultValue = "") String description) {
        return banking.deposit(id, auth.getName(), amount, description);
    }
    @PostMapping("/{id}/withdrawals")
    public BankAccount withdraw(@PathVariable Long id, Authentication auth,
            @RequestParam BigDecimal amount, @RequestParam(defaultValue = "") String description) {
        return banking.withdraw(id, auth.getName(), amount, description);
    }
    @PatchMapping("/{id}/status")
    public BankAccount changeStatus(@PathVariable Long id, @RequestParam String status) {
        return banking.changeStatus(id, status);
    }
}
