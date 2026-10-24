alter table event_log
drop column id;

alter table event_log
    add id bigint auto_increment primary key first;

alter table staff_information
drop column id;

alter table staff_information
    add id bigint auto_increment primary key first;

alter table supervisor_information
drop column id;

alter table supervisor_information
    add id bigint auto_increment primary key first;