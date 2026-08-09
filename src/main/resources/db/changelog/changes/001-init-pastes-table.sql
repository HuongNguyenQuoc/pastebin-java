--liquibase formatted sql

--changeset huong:1
CREATE TABLE pastes
(
    shortlink                    VARCHAR(7) PRIMARY KEY,
    content                      TEXT      NOT NULL,
    expiration_length_in_minutes INT,
    created_at                   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pastes_created_at ON pastes (created_at);