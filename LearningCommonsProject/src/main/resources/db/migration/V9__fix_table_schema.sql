ALTER TABLE staff_information
    MODIFY Current_Points INT NOT NULL DEFAULT 65, -- tinyint causes crashes above 127, so we use int instead

    MODIFY Position_2 VARCHAR(255) NULL,-- this field should be nullable, but it is not, so we have to change it
    MODIFY Supervisor_2 VARCHAR(255) NULL, -- same as above, non-nullable will cause crashes when there is no value
    -- these (Position_2 & Supervisor_2) columns should not exist, keys should be used to accomplish what I think this is trying to do

    ADD COLUMN is_returning BOOLEAN NOT NULL DEFAULT FALSE AFTER Current_Points, -- needed to determine if a staff member is returning or not (duh, lol)

    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE AFTER is_returning; -- this column indicates if a staff member has been fired without deleting the data

ALTER TABLE supervisor_information
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE; -- provided for the same reason as the active column in staff_information