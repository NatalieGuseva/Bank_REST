--liquibase formatted sql

--changeset author:create-cards-table
--comment: Создание таблицы банковских карт
CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       encrypted_card_number VARCHAR(255) UNIQUE NOT NULL,
                       user_id BIGINT NOT NULL REFERENCES users(id),
                       expiry_date DATE NOT NULL CHECK (expiry_date > CURRENT_DATE),
                       status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
                       balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0)
);

--rollback DROP TABLE cards;