INSERT INTO tasks (id, title, description, project_id, creation_date, dead_line_date, deleted)
VALUES (1, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 1, '2022-07-05',
        '2022-08-05', false),
       (2, 'Design', 'Perform design, selection of technologies and workers for project implementation', 1,
        '2022-07-05', '2022-09-05', false),
       (3, 'Implementation', 'Implement the project according to all requirements and design', 1, '2022-07-05',
        '2022-10-09', false),
       (4, 'Testing', 'Test all implemented functionality and check compliance with customer requirements',
        1,
        '2022-07-05', '2022-11-05', false),
       (5, 'Introduction', 'Present and implement the developed software', 1, '2022-07-05', '2022-12-20', false),
       (16, 'Test', 'Test test', 1, '2022-07-05', '2022-12-20', true),

       (6, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 2, '2022-07-05',
        '2022-08-05', false),
       (7, 'Design', 'Perform design, selection of technologies and workers for project implementation', 2,
        '2022-07-05', '2022-09-05', false),
       (8, 'Implementation', 'Implement the project according to all requirements and design', 2, '2022-07-05',
        '2022-10-09', false),
       (9, 'Testing', 'Test all implemented functionality and check compliance with customer requirements', 2,
        '2022-07-05', '2022-11-05', false),
       (10, 'Introduction', 'Present and implement the developed software', 2, '2022-07-05', '2022-12-20', false),

       (11, 'Formation of requirements',
        'Get project requirements from the customer, discuss them with the employees team', 3, '2022-07-05',
        '2022-08-05', false),
       (12, 'Design', 'Perform design, selection of technologies and workers for project implementation', 3,
        '2022-07-05', '2022-09-05', false),
       (13, 'Implementation', 'Implement the project according to all requirements and design', 3, '2022-07-05',
        '2022-10-09', false),
       (14, 'Testing', 'Test all implemented functionality and check compliance with customer requirements', 3,
        '2022-07-05', '2022-11-05', false),
       (15, 'Introduction', 'Present and implement the developed software', 3, '2022-07-05', '2022-12-20', false);

SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));

INSERT INTO tasks_statuses(task_id, statuses)
VALUES (1, 'CREATED'),
       (1, 'OPEN'),
       (2, 'CREATED'),
       (2, 'OPEN'),
       (3, 'CREATED'),
       (4, 'CREATED'),
       (5, 'CREATED'),
       (6, 'CREATED'),
       (7, 'CREATED'),
       (8, 'CREATED'),
       (9, 'CREATED'),
       (10, 'CREATED'),
       (11, 'CREATED'),
       (12, 'CREATED'),
       (13, 'CREATED'),
       (14, 'CREATED'),
       (15, 'CREATED');


