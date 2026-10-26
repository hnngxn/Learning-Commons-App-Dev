DROP TABLE IF EXISTS event_log;

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