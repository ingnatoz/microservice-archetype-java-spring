CREATE TABLE tasks (
    id         RAW(16)                     NOT NULL,
    title      VARCHAR2(120 CHAR)          NOT NULL,
    done       BOOLEAN                     NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT tasks_pk PRIMARY KEY (id)
);

CREATE INDEX tasks_created_at_ix ON tasks (created_at DESC, id);
