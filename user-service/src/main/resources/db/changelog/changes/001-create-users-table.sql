--liquibase formatted sql
--changeset mykidevs:1
CREATE SEQUENCE user_seq_id START WITH 1 INCREMENT BY 20;

--changeset mykidevs:2
CREATE TABLE  IF NOT EXISTS users(
    id BIGINT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(30) NOT NULL,
    description VARCHAR(100) NOT NULL,
    has_verified_email BOOLEAN NOT NULL DEFAULT FALSE
);
--rollback DROP TABLE users
--rollback DROP SEQUENCE user_seq_id;