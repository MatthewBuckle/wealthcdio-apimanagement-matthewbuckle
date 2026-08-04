package com.banking.transaction_processor.repository;


import com.banking.transaction_processor.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_IdOrderByTimestampDesc(Long accountId);
    List<Transaction> findByAccount_IdAndTimestampBetweenOrderByTimestampDesc(Long accountId, LocalDateTime fromDateTime, LocalDateTime toDateTime);
}