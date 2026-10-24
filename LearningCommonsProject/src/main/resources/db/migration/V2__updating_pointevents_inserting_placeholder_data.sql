alter table point_events
    add id bigint auto_increment primary key first;

alter table point_events
    add min_points int not null;

alter table point_events
    add max_points int not null;

alter table point_events
    modify Default_Point_Value varchar(255) not null;


insert into point_events (Event_Name, Event_Type, Default_Point_Value, min_points, max_points) values
    ('Job Performance - Bonus', 'Bonus', +(1-10), 1, 10),
    ('Leadership - Bonus', 'Bonus', +(5-15), 5, 15),
    ('Office Service - Bonus', 'Bonus', +(5-5), 5, 5),
    ('Professionalism and Office Procedure - Bonus', 'Bonus', +(5-5), 5, 5),
    ('Professionalism and Office Procedure - Infraction', 'Infraction', -(20-1), -20, -1),
    ('Scheduling - Bonus', 'Bonus', +(5-15), 5, 15),
    ('Scheduling - Infraction', 'Infraction', -(20-5), -20, -5),
    ('Timekeeping - Infraction', 'Infraction', -(5-1), -5, -1);