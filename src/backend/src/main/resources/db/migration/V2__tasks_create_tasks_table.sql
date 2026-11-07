-- Module: tasks. Table for the Task entity (06-data/models.md).
CREATE TABLE tasks (
    id           UUID                     PRIMARY KEY,
    owner_id     UUID                     NOT NULL,
    title        VARCHAR(120)             NOT NULL,
    description  VARCHAR(1000),
    status       VARCHAR(10)              NOT NULL DEFAULT 'PENDING',
    due_date     DATE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_tasks_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_tasks_title_not_blank CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT ck_tasks_status CHECK (status IN ('PENDING', 'COMPLETED')),
    CONSTRAINT ck_tasks_completed_at CHECK (
        (status = 'COMPLETED' AND completed_at IS NOT NULL)
        OR (status = 'PENDING' AND completed_at IS NULL)
    )
);

CREATE INDEX idx_tasks_owner_id_due_date ON tasks (owner_id, due_date);
CREATE INDEX idx_tasks_owner_id_status ON tasks (owner_id, status);
