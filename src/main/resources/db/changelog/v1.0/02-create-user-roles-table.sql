--liquibase formatted sql

--changeset author:create-user-roles-table
--comment: Создание таблицы ролей пользователей
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role VARCHAR(10) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
                            PRIMARY KEY (user_id, role)
);

--rollback DROP TABLE user_roles;