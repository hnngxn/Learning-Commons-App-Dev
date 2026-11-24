DROP TABLE IF EXISTS Event_Log;

CREATE TABLE Event_Log (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           staff_id BIGINT NULL,
                           point_event_id BIGINT NOT NULL,
                           points_applied INT NOT NULL,
                           event_date DATE NOT NULL,
                           comments VARCHAR(255),
                           CONSTRAINT fk_event_log_staff
                               FOREIGN KEY (staff_id) REFERENCES Staff_Information(id),
                           CONSTRAINT fk_event_log_point_event
                               FOREIGN KEY (point_event_id) REFERENCES Point_Events(id)
);

INSERT INTO Event_Log (staff_id, point_event_id, points_applied, event_date, comments)
SELECT NULL, pe.id, 10, CURRENT_DATE, 'Initial seeded event log entry'
FROM Point_Events pe
ORDER BY pe.id ASC
    LIMIT 1;