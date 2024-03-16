-- liquibase formatted sql

-- changeset v.aliyev:1
CREATE TABLE notification_task (
    id BIGSERIAL PRIMARY KEY,
    datetime TIMESTAMP NOT NULL,
    chatId BIGINT NOT NULL,
    message VARCHAR(512)
);