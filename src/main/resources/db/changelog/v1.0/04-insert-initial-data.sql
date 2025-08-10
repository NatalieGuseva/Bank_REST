--liquibase formatted sql

--changeset author:insert-initial-data
--comment: Инициализация тестовых данных
INSERT INTO users (username, password) VALUES
                                           ('admin', '$2a$10$xJwL5vWm6UZ4RvZ7bq.5XeBqjQGq9Yb9tTkR7Nc1V1dJkK5XrOJ6a'), -- admin123
                                           ('user', '$2a$10$YH2ZJ5cW6UZ4RvZ7bq.5XeBqjQGq9Yb9tTkR7Nc1V1dJkK5XrOJ6b'); -- user123

INSERT INTO user_roles (user_id, role) VALUES
                                           (1, 'ADMIN'), (1, 'USER'), (2, 'USER');

--rollback DELETE FROM user_roles;
--rollback DELETE FROM users;