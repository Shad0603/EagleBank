package com.app.eaglebank.repository;

import com.app.eaglebank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccount_AccountNumber(String accountNumber);
}
