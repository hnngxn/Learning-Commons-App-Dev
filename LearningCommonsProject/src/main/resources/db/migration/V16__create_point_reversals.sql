CREATE TABLE point_reversals (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 event_log_id BIGINT NOT NULL,
                                 points_adjustment INT NOT NULL,
                                 reason VARCHAR(255) NOT NULL,
                                 reversed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT uq_point_reversal_event UNIQUE (event_log_id),
                                 CONSTRAINT fk_point_reversal_event
                                     FOREIGN KEY (event_log_id) REFERENCES event_log(id)
);