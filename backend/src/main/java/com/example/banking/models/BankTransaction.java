package com.example.banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;
    @Column(nullable = false, length = 20)
    private String type;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balanceAfter;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(nullable = false, length = 36)
    private String reference;

    public BankTransaction() { }
    public BankTransaction(BankAccount account, String type, BigDecimal amount,
            String description, String reference) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = account.getBalance();
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        this.description = description;
        this.reference = reference;
    }
    public Long getId() { return id; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getDescription() { return description; }
    public String getReference() { return reference; }
}
