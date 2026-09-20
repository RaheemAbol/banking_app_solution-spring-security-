package com.example.banking.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;
    @Column(nullable = false, length = 20)
    private String accountType;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, length = 20)
    private String status;

    public BankAccount() { }
    public BankAccount(AppUser owner, String accountType) {
        this.owner = owner;
        this.accountType = accountType;
        this.balance = new BigDecimal("0.00");
        this.status = "ACTIVE";
    }
    public Long getId() { return id; }
    public AppUser getOwner() { return owner; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(String status) { this.status = status; }
}
