--liquibase formatted sql

--changeset author:create-users-table
--comment: Создание таблицы пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(60) NOT NULL -- BCrypt hash
);

--rollback DROP TABLE users;