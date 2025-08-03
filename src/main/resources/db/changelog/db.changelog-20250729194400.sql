--liquibase formatted sql

--changeset daurenassanbaev:2
CREATE TABLE IF NOT EXISTS files
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT       NOT NULL REFERENCES users (id),
    filename          VARCHAR(255) NOT NULL,
    s3_key            VARCHAR(500) NOT NULL UNIQUE,
    file_size         BIGINT       NOT NULL,
    content_type      VARCHAR(100) NOT NULL,
    is_public         BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at  TIMESTAMP
)