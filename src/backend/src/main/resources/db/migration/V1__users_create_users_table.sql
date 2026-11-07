-- Module: users. Table for the User entity (06-data/models.md).
CREATE TABLE users (
    id            UUID                     PRIMARY KEY,
    email         VARCHAR(254)             NOT NULL,
    password_hash VARCHAR(100)             NOT NULL,
    role          VARCHAR(10)              NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'USER'))
);
