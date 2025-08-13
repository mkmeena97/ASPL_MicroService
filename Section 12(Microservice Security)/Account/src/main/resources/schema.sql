-- 1. CUSTOMER TABLE
CREATE TABLE IF NOT EXISTS customer (
  customer_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  mobile_number VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(20) NOT NULL,
  updated_at TIMESTAMP,
  updated_by VARCHAR(20)
);

-- 2. ACCOUNTS TABLE
CREATE TABLE IF NOT EXISTS accounts (
  account_number VARCHAR(12) PRIMARY KEY,
  customer_id INT NOT NULL,
  account_type VARCHAR(50) NOT NULL,
  branch_address VARCHAR(200) NOT NULL,
  balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(20) NOT NULL,
  updated_at TIMESTAMP,
  updated_by VARCHAR(20),
  CONSTRAINT fk_customer_id FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 3. TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS transactions (
  transaction_id INT AUTO_INCREMENT PRIMARY KEY,
  account_number INT NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount DECIMAL(18,2) NOT NULL,
  description VARCHAR(255),
  related_account INT,
  created_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_account_number FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- 4. ACCOUNT TYPES TABLE
CREATE TABLE IF NOT EXISTS account_types (
  type_code VARCHAR(50) PRIMARY KEY,
  description VARCHAR(255)
);

-- Insert basic account types - H2 uses MERGE INTO for upsert
MERGE INTO account_types (type_code, description) VALUES
  ('SAVINGS', 'Savings Account');
MERGE INTO account_types (type_code, description) VALUES
  ('CURRENT', 'Current Account');
MERGE INTO account_types (type_code, description) VALUES
  ('SALARY', 'Salary Account');
MERGE INTO account_types (type_code, description) VALUES
  ('NRI', 'Non-Resident External Account');
