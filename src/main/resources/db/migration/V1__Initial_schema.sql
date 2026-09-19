CREATE TABLE customer (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    risk_rating VARCHAR(50),
    kyc_status VARCHAR(50)
);

CREATE TABLE account (
    id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    account_type VARCHAR(50),
    currency VARCHAR(10),
    opening_date DATE,
    status VARCHAR(50)
);

CREATE TABLE transaction (
    id VARCHAR(50) PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(10),
    counterparty_id VARCHAR(50),
    counterparty_name VARCHAR(255),
    channel VARCHAR(50),
    timestamp TIMESTAMP,
    jurisdiction VARCHAR(50),
    direction VARCHAR(20)
);

CREATE TABLE alert (
    id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50),
    account_id VARCHAR(50),
    triggered_rule_name VARCHAR(100),
    risk_score INT,
    description TEXT,
    timestamp TIMESTAMP,
    status VARCHAR(50)
);

CREATE TABLE alert_transaction_ids (
    alert_id VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (alert_id) REFERENCES alert(id) ON DELETE CASCADE
);

CREATE INDEX idx_transaction_account_id ON transaction(account_id);
CREATE INDEX idx_account_customer_id ON account(customer_id);
CREATE INDEX idx_alert_account_id ON alert(account_id);
