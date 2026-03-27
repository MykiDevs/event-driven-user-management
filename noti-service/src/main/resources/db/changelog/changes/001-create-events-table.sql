--liquibase formatted sql
--changeset mykidevs:1
CREATE TABLE  IF NOT EXISTS events (
    uuid UUID PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    event_status VARCHAR(50) NOT NULL
)
--rollback DROP TABLE events;