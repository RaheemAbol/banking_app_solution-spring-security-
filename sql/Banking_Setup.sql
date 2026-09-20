-- Run this complete script in MySQL Workbench before starting the backend.
-- Re-running it preserves balances and history for the next lesson.
CREATE DATABASE IF NOT EXISTS banking_app;
USE banking_app;

CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    display_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bank_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_bank_account_owner FOREIGN KEY (owner_id) REFERENCES app_users(id),
    CONSTRAINT chk_bank_balance CHECK (balance >= 0),
    CONSTRAINT chk_bank_type CHECK (account_type IN ('CHECKING','SAVINGS')),
    CONSTRAINT chk_bank_status CHECK (status IN ('ACTIVE','FROZEN'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bank_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    balance_after DECIMAL(14,2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    description VARCHAR(200) NOT NULL,
    reference VARCHAR(36) NOT NULL,
    CONSTRAINT fk_bank_transaction_account FOREIGN KEY (account_id) REFERENCES bank_accounts(id),
    CONSTRAINT chk_bank_transaction_amount CHECK (amount > 0),
    CONSTRAINT chk_bank_transaction_balance CHECK (balance_after >= 0),
    INDEX idx_bank_transactions_account (account_id, id)
) ENGINE=InnoDB;

-- DemoData creates three demo logins and opening transactions on first startup.
-- Passwords are encoded with BCrypt by Java. No plaintext passwords are stored here.
