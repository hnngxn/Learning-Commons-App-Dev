ALTER TABLE event_log
    ADD COLUMN submission_token VARCHAR(36) NULL,
    ADD COLUMN balance_after INT NULL,
    ADD COLUMN position_at_entry VARCHAR(255) NULL,
    ADD COLUMN supervisor_at_entry VARCHAR(255) NULL,
    ADD COLUMN entered_by VARCHAR(255) NULL,
    ADD CONSTRAINT uq_event_submission_token
        UNIQUE (submission_token);