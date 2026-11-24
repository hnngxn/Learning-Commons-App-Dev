CREATE TABLE semester_point_resets (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       semester_year SMALLINT NOT NULL,
                                       semester_term VARCHAR(6) NOT NULL,
                                       reset_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT uq_reset_semester
                                           UNIQUE (semester_year, semester_term),

                                       CONSTRAINT chk_reset_term
                                           CHECK (semester_term IN ('SPRING', 'SUMMER', 'FALL'))
);

CREATE TABLE semester_point_reset_entries (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              reset_id BIGINT NOT NULL,
                                              staff_id BIGINT NOT NULL,
                                              employee_status VARCHAR(9) NOT NULL,
                                              previous_points INT NOT NULL,
                                              starting_points INT NOT NULL,

                                              CONSTRAINT uq_reset_staff
                                                  UNIQUE (reset_id, staff_id),

                                              CONSTRAINT fk_reset_entry_reset
                                                  FOREIGN KEY (reset_id)
                                                      REFERENCES semester_point_resets(id),

                                              CONSTRAINT fk_reset_entry_staff
                                                  FOREIGN KEY (staff_id)
                                                      REFERENCES staff_information(id),

                                              CONSTRAINT chk_reset_starting_points
                                                  CHECK (
                                                      (employee_status = 'NEW' AND starting_points = 65)
                                                          OR
                                                      (employee_status = 'RETURNING' AND starting_points = 70)
                                                      )
);