package com.example.banking.repositories;

import com.example.banking.models.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountIdOrderByIdDesc(Long accountId);
}
