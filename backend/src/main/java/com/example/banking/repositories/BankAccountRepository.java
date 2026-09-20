package com.example.banking.repositories;

import com.example.banking.models.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByOwnerEmailOrderByIdAsc(String email);
    List<BankAccount> findAllByOrderByIdAsc();
    Optional<BankAccount> findByIdAndOwnerEmail(Long id, String email);

    // Supplied banking infrastructure: serialize changes to this balance in MySQL.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findForUpdate(@Param("id") Long id);
}
