CREATE TABLE IF NOT EXISTS Programs (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        program_name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS staff_programs (
                                              staff_id BIGINT NOT NULL,
                                              program_id BIGINT NOT NULL,
                                              PRIMARY KEY (staff_id, program_id),
    CONSTRAINT fk_staff_programs_staff
    FOREIGN KEY (staff_id) REFERENCES Staff_Information(id),
    CONSTRAINT fk_staff_programs_program
    FOREIGN KEY (program_id) REFERENCES Programs(id)
    );

INSERT INTO Programs (program_name)
SELECT * FROM (
                  SELECT 'Academic Coaching' AS program_name
                  UNION ALL SELECT 'Success Skills Workshops'
                  UNION ALL SELECT 'Academic Writing Center'
                  UNION ALL SELECT 'Learning Assistants'
                  UNION ALL SELECT 'Learning Communities'
                  UNION ALL SELECT 'Math & Science Support Center'
                  UNION ALL SELECT 'Supplemental Review Sessions'
                  UNION ALL SELECT 'Content Review Sessions'
                  UNION ALL SELECT 'Supplemental Instruction'
                  UNION ALL SELECT 'Peer Tutoring'
              ) AS seed_programs
WHERE NOT EXISTS (
    SELECT 1 FROM Programs p WHERE p.program_name = seed_programs.program_name
);