CREATE TABLE IF NOT EXISTS Programs (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        programName VARCHAR(255) NOT NULL
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

INSERT INTO Programs (programName)
SELECT * FROM (
                  SELECT 'Peer Tutoring' AS programName
                  UNION ALL SELECT 'Supplemental Instruction'
                  UNION ALL SELECT 'Writing Center'
              ) AS seed_programs
WHERE NOT EXISTS (SELECT 1 FROM Programs);