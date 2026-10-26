CREATE TABLE Event_Log (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           staff_id BIGINT NOT NULL,
                           point_event_id BIGINT NOT NULL,
                           pointsApplied INT NOT NULL,
                           eventDate DATE,
                           comments VARCHAR(255),
                           FOREIGN KEY (staff_id) REFERENCES staff_information(id),
                           FOREIGN KEY (point_event_id) REFERENCES Point_Events(id)
);