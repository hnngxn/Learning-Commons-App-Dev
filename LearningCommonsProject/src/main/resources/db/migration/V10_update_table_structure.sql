/*
    This file will remove the redundant columns and table from the database.
    We do not need a separate supervisor table, and we do not want multiple columns
    to record a many-to-many relationship (i.e. multiple supervisor columns).
*/

-- Set up the primary key
ALTER TABLE staff_information
    MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- set an ID column as the primary key
    ADD COLUMN is_supervisor BOOLEAN NOT NULL DEFAULT FALSE AFTER is_returning; -- keeps track of who is a supervisor

-- MOST IMPORTANT PART!
-- this table is how the connection of staff and their supervisor(s) are kept track of
/*
    Whenever someone wants to assign a supervisor to a staff member (or even another supervisor)
    they will just create a new entry in this table. This can be done manually, or you can use backend code
    to tie this into a script that allows users to do this from a user interface.
 */
CREATE TABLE staff_supervisors (
    staff_id BIGINT NOT NULL,
    supervisor_id BIGINT NOT NULL,
    PRIMARY KEY (staff_id, supervisor_id),
    FOREIGN KEY (staff_id) REFERENCES staff_information(id) ON DELETE CASCADE,
    FOREIGN KEY (supervisor_id) REFERENCES staff_information(id) ON DELETE CASCADE
);-- this just creates the table, it doesn't create the linking



-- Uncomment the below code block if you need to copy over existing supervisor information
/*
INSERT INTO staff_information (Full_Name, Email, Position_1, Supervisor_1, Current_Points, is_returning, is_supervisor, active)
SELECT
    Full_Name,
    Email,
    Position,
    '',      -- Empty string in case Supervisor_1 is still NOT NULL
    65,      -- Default points
    FALSE,   -- Default is_returning
    TRUE,    -- Flag them as a supervisor
    active
FROM supervisor_information;
*/



-- Uncomment the below code block if you need to copy over existing supervisor information
-- this block specifically copies over any data that was in the supervisor columns and table into the new one
/*
INSERT INTO staff_supervisors (staff_id, supervisor_id)
SELECT staff.id, sup.id
FROM staff_information staff
         JOIN staff_information sup
              ON (staff.Supervisor_1 = sup.Full_Name OR staff.Supervisor_1 = sup.Email)
WHERE sup.is_supervisor = TRUE;

INSERT IGNORE INTO staff_supervisors (staff_id, supervisor_id)
SELECT staff.id, sup.id
FROM staff_information staff
         JOIN staff_information sup
              ON (staff.Supervisor_2 = sup.Full_Name OR staff.Supervisor_2 = sup.Email)
WHERE sup.is_supervisor = TRUE;
*/



-- drop the unneeded columns from the staff info table
ALTER TABLE staff_information
DROP COLUMN Supervisor_1,
    DROP COLUMN Supervisor_2;

-- drop the unneeded supervisor table
DROP TABLE supervisor_information;
