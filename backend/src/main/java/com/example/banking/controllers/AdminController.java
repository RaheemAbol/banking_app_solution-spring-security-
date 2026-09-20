package com.example.banking.controllers;

import com.example.banking.models.BankAccount;
import com.example.banking.services.BankingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final BankingService banking;
    public AdminController(BankingService banking) { this.banking = banking; }
    @GetMapping("/accounts")
    public List<BankAccount> accounts() { return banking.allAccounts(); }
}
