CREATE TABLE IF NOT EXISTS programs (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        program_name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS staff_programs (
                                              staff_id BIGINT NOT NULL,
                                              program_id BIGINT NOT NULL,
                                              PRIMARY KEY (staff_id, program_id),
    CONSTRAINT fk_staff_programs_staff FOREIGN KEY (staff_id) REFERENCES staff_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_staff_programs_program FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE
    );

INSERT INTO programs (program_name) VALUES
                                        ('Supplemental Instruction (SI)'),
                                        ('Peer Tutoring'),
                                        ('Academic Coaching'),
                                        ('Math & Science Support Center (MASS)'),
                                        ('Writing Center');