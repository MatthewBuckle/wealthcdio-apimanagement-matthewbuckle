package com.banking.transaction_processor.repository;

import com.banking.transaction_processor.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}