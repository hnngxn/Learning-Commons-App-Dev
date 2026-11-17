-- 1. Promote `id` to Primary Key first (separate statement avoids MySQL FK creation errors)
ALTER TABLE staff_information
    MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY;

-- 2. Add self-referencing `supervisor_id` FK and `is_supervisor` flag
ALTER TABLE staff_information
    ADD COLUMN supervisor_id BIGINT NULL AFTER Email,
    ADD COLUMN is_supervisor BOOLEAN NOT NULL DEFAULT FALSE AFTER is_returning,
    ADD CONSTRAINT fk_staff_supervisor
        FOREIGN KEY (supervisor_id) REFERENCES staff_information(id) ON DELETE SET NULL;

-- 3. Move supervisors from `supervisor_information` into `staff_information`
-- Preserves the `active` column you added in V9
INSERT INTO staff_information (Full_Name, Email, Position_1, Supervisor_1, Current_Points, is_returning, is_supervisor, active)
SELECT
    Full_Name,
    Email,
    Position,
    '',
    65,
    FALSE,
    TRUE,
    active
FROM supervisor_information;

-- 4. Link staff members to their supervisor's new `id`
UPDATE staff_information staff
    INNER JOIN staff_information sup
ON (staff.Supervisor_1 = sup.Full_Name OR staff.Supervisor_1 = sup.Email)
    SET staff.supervisor_id = sup.id
WHERE sup.is_supervisor = TRUE;

-- 5. Remove numbered columns (`_1` / `_2`) and rename `Position_1` -> `Position`
ALTER TABLE staff_information
    CHANGE COLUMN Position_1 Position VARCHAR(255) NOT NULL,
DROP COLUMN Position_2,
    DROP COLUMN Supervisor_1,
    DROP COLUMN Supervisor_2;

-- 6. Drop the redundant table
DROP TABLE supervisor_information;
