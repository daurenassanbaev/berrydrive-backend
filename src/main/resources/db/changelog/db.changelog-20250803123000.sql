--liquibase formatted sql

--changeset daurenassanbaev:3
CREATE TABLE IF NOT EXISTS shared_links
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    file_id    BIGINT       NOT NULL REFERENCES files (id) ON DELETE CASCADE,
    slug       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
)