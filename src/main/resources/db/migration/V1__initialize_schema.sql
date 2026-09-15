CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    booking_date DATE NOT NULL,
    category VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    description VARCHAR(500)
);

ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS category_color VARCHAR(7) DEFAULT '#6c757d';

UPDATE transactions
SET category_color = '#6c757d'
WHERE category_color IS NULL;

ALTER TABLE transactions
    ALTER COLUMN category_color SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_transactions_booking_date
    ON transactions (booking_date);

CREATE INDEX IF NOT EXISTS idx_transactions_type
    ON transactions (transaction_type);

CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_app_users_username
    ON app_users (username);

