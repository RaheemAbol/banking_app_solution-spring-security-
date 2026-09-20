package com.example.banking.controllers;

import com.example.banking.services.BankingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final BankingService banking;
    public TransferController(BankingService banking) { this.banking = banking; }
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(Authentication auth, @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId, @RequestParam BigDecimal amount) {
        banking.transfer(auth.getName(), fromAccountId, toAccountId, amount);
    }
}
