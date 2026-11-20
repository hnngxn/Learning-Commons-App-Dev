alter table staff_information
    modify Current_Points smallint not null;

insert into staff_information (Full_Name, Email, Position_1, Supervisor_1, Position_2, Supervisor_2, Current_Points) values
            ("Maria Thomas", "mthomas@gmail.com", "Tutor", "Dave Hernandez", "", "Mary Pilton", 95),
            ("John Doe", "jdoe@gmail.com", "Learning Center", "Dave Hernandez", "Professor Assistant", "Mary Pilton", 115),
            ("Jane Doe", "djane@gmail.com", "High School Mentor", "Dave Hernandez", "High School Assistant", "Mary Pilton", 40),
            ("Jake Thomas", "jthomas@gmail.com", "Tutor", "Dave Hernandez", "", "Mary Pilton", 60),
            ("Allen Campbell", "acampbell@gmail.com", "Learning Center", "Dave Hernandez", "Professor Assistant", "Mary Pilton", 80),
            ("April Dekok", "dapril@gmail.com", "High School Mentor", "Dave Hernandez", "High School Assistant", "Mary Pilton", 95),
            ("March Dekok", "dmarch@gmail.com", "Tutor", "Dave Hernandez", "", "Mary Pilton", 10),
            ("January Dekok", "djanuary@gmail.com", "Learning Center", "Dave Hernandez", "Professor Assistant", "Mary Pilton", 50),
            ("May Dekok", "dmay@gmail.com", "High School Mentor", "Dave Hernandez", "High School Assistant", "Mary Pilton", 100),
            ("Julius Dekok", "djulius@gmail.com", "Tutor", "Dave Hernandez", "", "Mary Pilton", 75),
            ("Augustus Dekok", "daugustus@gmail.com", "Learning Center", "Dave Hernandez", "Professor Assistant", "Mary Pilton", 150),
            ("June Dekok", "djune@gmail.com", "High School Mentor", "Dave Hernandez", "High School Assistant", "Mary Pilton", 20)