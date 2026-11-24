CREATE TABLE Programs (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          programName VARCHAR(255)
);

CREATE TABLE staff_programs (
                                staff_id BIGINT NOT NULL,
                                program_id BIGINT NOT NULL,
                                PRIMARY KEY (staff_id, program_id),
                                FOREIGN KEY (staff_id) REFERENCES staff_information(id),
                                FOREIGN KEY (program_id) REFERENCES Programs(id)
);