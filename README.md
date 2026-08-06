# Banking Transaction Processor

A Spring Boot REST API application that simulates a banking transaction processing system. The application supports account management, deposits, withdrawals, transfers, balance inquiries, and transaction history tracking while ensuring data integrity and validation.

## Features

### Account Management
- Create bank accounts with unique account IDs.
- Maintain account balances.
- Retrieve account details and current balance.

### Transaction Processing
- Deposit funds into an account.
- Withdraw funds from an account.
- Transfer funds between accounts.

### Validation
- Prevent overdraft transactions.
- Reject invalid transaction amounts (zero or negative).
- Validate account existence before processing transactions.

### Transaction Ledger
- Record all transactions with timestamps.
- Maintain transaction history per account.
- Support audit-friendly transaction tracking.

### REST APIs
- Query account balances.
- Retrieve transaction history for an account.
- Process banking operations through REST endpoints.

---

## Technology Stack

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Hibernate
- MySQL Database
- Maven
- Lombok
---

## Project Structure

```text
src/main/java/com/banking/transaction_processor

├── controller
│   ├── AccountController.java
│   ├── TransactionController.java
│
├── repository
│   ├── AccountRepository.java
│   ├── TransactionRepository.java
│
├── entity
│   ├── Account.java
│   ├── Transaction.java
│
├── dto
│   ├── AccountResponse.java
│   ├── ApiResponse.java
│   ├── CreateAccountRequest.java
│   ├── DepositRequest.java
│   ├── WithdrawRequest.java
│   ├── TransferRequest.java
│
├── service
│   ├── AccountService.java
│   ├── TransactionService.java
│
├── exception
│   ├── AccountNotFoundException.java
│   ├── InsufficientFundsException.java
│   ├── GlobalExceptionHandler.java
│
└── TransactionProcessorApplication.java
```

---

## Entity Overview

### Account

Represents a bank account.

Attributes:
- id
- accountHolderName
- email
- balance

### Transaction

Represents a banking transaction.

Attributes:
- id
- transactionType
- amount
- timestamp
- referenceNumber

Transaction types:
- DEPOSIT
- WITHDRAWAL
- TRANSFER


## Running the Application

### Clone the Repository

```bash
git clone https://github.com/<your-username>/banking-transaction-processor.git
```

### Navigate to Project Directory

```bash
cd banking-transaction-processor
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

---

## Testing

Run all unit tests:

```bash
mvn test
```

---

## Future Enhancements

- User authentication and authorization (Spring Security + JWT)
- Account locking mechanism
- Scheduled transaction support
- Transaction reversal capability
- Pagination for transaction history
- Audit logging and monitoring
- Docker deployment
- Microservices architecture support

---

## Design Highlights

- Follows layered architecture.
- Uses Spring Data JPA for persistence.
- Transaction-safe operations using `@Transactional`.
- Separate ledger maintained through transaction records.
- Extensible design for future banking features.

---

## Author

**Matthew Buckle A**  
Lead Software Development Engineer
