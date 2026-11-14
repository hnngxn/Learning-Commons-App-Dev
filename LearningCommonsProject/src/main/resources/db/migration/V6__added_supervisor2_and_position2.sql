alter table staff_information
    add Position_2 varchar(255) not null after Supervisor_1;

alter table staff_information
    add Supervisor_2 varchar(255) not null after Position_2;
