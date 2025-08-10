--liquibase formatted sql

--changeset author:create-indexes
--comment: Создание базовых индексов
CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_status ON cards(status);

--rollback DROP INDEX idx_cards_user_id, idx_cards_status;