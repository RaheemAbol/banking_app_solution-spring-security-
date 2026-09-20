package com.example.banking.config;

import com.example.banking.models.*;
import com.example.banking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.util.UUID;

@Configuration
public class DemoData {
    @Bean
    CommandLineRunner demoAccounts(AppUserRepository users, BankAccountRepository accounts,
            BankTransactionRepository transactions, PasswordEncoder encoder,
            TransactionTemplate tx) {
        return args -> tx.executeWithoutResult(status -> {
            // Classroom fixtures only. Existing users, roles and balances are never reset.
            customer(users, accounts, transactions, encoder, "Casey Brooks",
                    "casey.bank@example.test", "500.00", "1000.00");
            customer(users, accounts, transactions, encoder, "Jordan Reed",
                    "jordan.bank@example.test", "200.00", "300.00");
            if (!users.existsByEmail("admin.bank@example.test")) {
                users.save(new AppUser("Bank Administrator", "admin.bank@example.test",
                        encoder.encode("BankDemo!2026"), "ADMIN"));
            }
        });
    }

    private void customer(AppUserRepository users, BankAccountRepository accounts,
            BankTransactionRepository transactions, PasswordEncoder encoder,
            String name, String email, String checking, String savings) {
        if (users.existsByEmail(email)) return;
        AppUser user = users.save(new AppUser(name, email, encoder.encode("BankDemo!2026"), "CUSTOMER"));
        opening(accounts, transactions, user, "CHECKING", checking);
        opening(accounts, transactions, user, "SAVINGS", savings);
    }

    private void opening(BankAccountRepository accounts, BankTransactionRepository transactions,
            AppUser user, String type, String amount) {
        BankAccount account = new BankAccount(user, type);
        account.setBalance(new BigDecimal(amount));
        accounts.save(account);
        transactions.save(new BankTransaction(account, "OPENING", new BigDecimal(amount),
                "Opening demo balance", UUID.randomUUID().toString()));
    }
}
