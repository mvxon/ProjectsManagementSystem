INSERT INTO tasks (id, title, description, status, project_id, creation_date, dead_line_date)
VALUES (1, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 'OPEN', 1, '2022-07-05',
        '2022-08-05'),
       (2, 'Design', 'Perform design, selection of technologies and workers for project implementation', 'OPEN', 1,
        '2022-07-05', '2022-09-05'),
       (3, 'Implementation', 'Implement the project according to all requirements and design', 'OPEN', 1, '2022-07-05',
        '2022-10-09'),
       (4, 'Testing', 'Test all implemented functionality and check compliance with customer requirements', 'OPEN',
        1,
        '2022-07-05', '2022-11-05'),
       (5, 'Introduction', 'Present and implement the developed software', 'OPEN', 1, '2022-07-05', '2022-12-20'),
       (16, 'Test', 'Test test', 'DOCUMENTED', 1, '2022-07-05', '2022-12-20'),

       (6, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 'IN_PROGRESS', 2, '2022-07-05',
        '2022-08-05'),
       (7, 'Design', 'Perform design, selection of technologies and workers for project implementation', 'IN_PROGRESS', 2,
        '2022-07-05', '2022-09-05'),
       (8, 'Implementation', 'Implement the project according to all requirements and design', 'IN_PROGRESS', 2, '2022-07-05',
        '2022-10-09'),
       (9, 'Testing', 'Test all implemented functionality and check compliance with customer requirements', 'OPEN', 2,
        '2022-07-05', '2022-11-05'),
       (10, 'Introduction', 'Present and implement the developed software', 'IN_PROGRESS', 2, '2022-07-05', '2022-12-20'),

       (11, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 'CORRECTED', 3, '2022-07-05',
        '2022-08-05'),
       (12, 'Design', 'Perform design, selection of technologies and workers for project implementation', 'OPEN', 3,
        '2022-07-05', '2022-09-05'),
       (13, 'Implementation', 'Implement the project according to all requirements and design', 'CREATED', 3, '2022-07-05',
        '2022-10-09'),
       (14, 'Testing', 'Test all implemented functionality and check compliance with customer requirements', 'CREATED', 3,
        '2022-07-05', '2022-11-05'),
       (15, 'Introduction', 'Present and implement the developed software', 'CREATED', 3, '2022-07-05', '2022-12-20');

SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));

