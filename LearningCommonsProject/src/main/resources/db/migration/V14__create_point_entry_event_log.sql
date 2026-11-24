-- Integration proposal for a fresh database using the staff migration lineage.
-- Preserve the legacy name/email/position table; do not discard its rows.
RENAME TABLE event_log TO legacy_event_log;

CREATE TABLE event_log (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           staff_id BIGINT NOT NULL,
                           point_event_id BIGINT NOT NULL,
                           points_applied INT NOT NULL,
                           event_date DATE,
                           comments VARCHAR(255),
                           FOREIGN KEY (staff_id) REFERENCES staff_information(id),
                           FOREIGN KEY (point_event_id) REFERENCES point_events(id)
);