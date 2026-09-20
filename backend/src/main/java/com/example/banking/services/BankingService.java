package com.example.banking.services;

import com.example.banking.models.BankAccount;
import com.example.banking.models.BankTransaction;
import com.example.banking.repositories.BankAccountRepository;
import com.example.banking.repositories.BankTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class BankingService {
    private static final Logger log = LoggerFactory.getLogger(BankingService.class);
    private static final BigDecimal MAX_BALANCE = new BigDecimal("999999999999.99");
    private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions;

    public BankingService(BankAccountRepository accounts,
            BankTransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public List<BankAccount> myAccounts(String email) {
        return accounts.findByOwnerEmailOrderByIdAsc(email);
    }

    public BankAccount myAccount(Long id, String email) {
        return accounts.findByIdAndOwnerEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account unavailable."));
    }

    public List<BankTransaction> history(Long id, String email) {
        myAccount(id, email);
        return transactions.findByAccountIdOrderByIdDesc(id);
    }

    @Transactional
    public BankAccount deposit(Long id, String email, BigDecimal amount, String description) {
        BigDecimal value = money(amount);
        String note = description(description, "Cash deposit");
        BankAccount account = ownedLocked(id, email);
        requireActive(account);
        BigDecimal newBalance = account.getBalance().add(value);
        requireBalanceFits(newBalance);
        account.setBalance(newBalance);
        transactions.save(new BankTransaction(account, "DEPOSIT", value,
                note, UUID.randomUUID().toString()));
        log.info("Deposit recorded for account id={}", id);
        return account;
    }

    @Transactional
    public BankAccount withdraw(Long id, String email, BigDecimal amount, String description) {
        BigDecimal value = money(amount);
        String note = description(description, "Cash withdrawal");
        BankAccount account = ownedLocked(id, email);
        requireActive(account);
        requireFunds(account, value);
        account.setBalance(account.getBalance().subtract(value));
        transactions.save(new BankTransaction(account, "WITHDRAWAL", value,
                note, UUID.randomUUID().toString()));
        log.info("Withdrawal recorded for account id={}", id);
        return account;
    }

    @Transactional
    public void transfer(String email, Long fromAccountId, Long toAccountId, BigDecimal amount) {
        BigDecimal value = money(amount);
        if (fromAccountId.equals(toAccountId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Choose two different accounts.");
        }
        // Consistent lock order prevents opposite transfers locking each other out.
        BankAccount first = ownedLocked(Math.min(fromAccountId, toAccountId), email);
        BankAccount second = ownedLocked(Math.max(fromAccountId, toAccountId), email);
        BankAccount from = first.getId().equals(fromAccountId) ? first : second;
        BankAccount to = first.getId().equals(toAccountId) ? first : second;
        requireActive(from);
        requireActive(to);
        requireFunds(from, value);
        requireBalanceFits(to.getBalance().add(value));
        String reference = UUID.randomUUID().toString();
        from.setBalance(from.getBalance().subtract(value));
        to.setBalance(to.getBalance().add(value));
        transactions.save(new BankTransaction(from, "TRANSFER_OUT", value,
                "Transfer to account " + to.getId(), reference));
        transactions.save(new BankTransaction(to, "TRANSFER_IN", value,
                "Transfer from account " + from.getId(), reference));
        log.info("Transfer recorded from account id={} to account id={}", fromAccountId, toAccountId);
    }

    public List<BankAccount> allAccounts() {
        return accounts.findAllByOrderByIdAsc();
    }

    @Transactional
    public BankAccount changeStatus(Long id, String status) {
        if (!"ACTIVE".equals(status) && !"FROZEN".equals(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use ACTIVE or FROZEN.");
        }
        BankAccount account = accounts.findForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account unavailable."));
        account.setStatus(status);
        log.info("Account id={} status changed to {}", id, status);
        return account;
    }

    private BankAccount ownedLocked(Long id, String email) {
        BankAccount account = accounts.findForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account unavailable."));
        if (!account.getOwner().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account unavailable.");
        }
        return account;
    }

    private BigDecimal money(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.compareTo(MAX_BALANCE) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a positive amount within the balance limit.");
        }
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use at most two decimal places.");
        }
    }

    private String description(String input, String fallback) {
        String value = input == null ? "" : input.trim();
        if (value.length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description must be 200 characters or fewer.");
        }
        return value.isEmpty() ? fallback : value;
    }

    private void requireActive(BankAccount account) {
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This account is frozen. Money cannot move until an administrator reactivates it.");
        }
    }

    private void requireFunds(BankAccount account, BigDecimal value) {
        if (account.getBalance().compareTo(value) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient funds. The balance has not changed.");
        }
    }

    private void requireBalanceFits(BigDecimal balance) {
        if (balance.compareTo(MAX_BALANCE) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The resulting balance exceeds the account limit.");
        }
    }
}
