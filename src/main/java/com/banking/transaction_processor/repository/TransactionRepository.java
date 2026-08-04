package com.banking.transaction_processor.repository;


import com.banking.transaction_processor.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_IdOrderByTimestampDesc(Long accountId);
}
