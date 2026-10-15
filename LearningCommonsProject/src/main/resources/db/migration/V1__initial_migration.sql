create table Event_Log
(
    Full_Name varchar(255) not null,
    Email     varchar(255) not null,
    Position  varchar(255) not null
);

create table Point_Events
(
    Event_Name          varchar(255) not null,
    Event_Type          varchar(255) not null,
    Default_Point_Value integer      not null
);

create table Staff_Information
(
    Full_Name      varchar(255) not null,
    Email          varchar(255) not null,
    Position_1     varchar(255) not null,
    Supervisor_1   varchar(255) not null,
    Current_Points tinyint      not null
);

create table Supervisor_Information
(
    Full_Name varchar(255) not null,
    Email     varchar(255) not null,
    Position  varchar(255) not null
);