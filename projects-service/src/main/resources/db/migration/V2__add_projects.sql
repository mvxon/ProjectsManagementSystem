INSERT INTO projects (id, name, title, description, customer, creation_date, dead_line_date, deleted)
VALUES (1, 'ProjectX', 'Secret military project',
        'Project for military vehicles accounting, technologies to use: Java, ReactJs',  'USArmy',
        '2021-02-20', '2023-02-23', false),
       (2, 'EShop', 'Online clothes shop',
        'Online clothes shop with opportunity to order clothes, technologies to use: Python, .NET, PHP',
        'MFashion', '2022-05-15', '2023-03-12', false),
       (3, 'Yandex Taxi', 'Taxi application project',
        'Taxi application with ability to order taxi online. Technologies to use: Ruby, Java, Android, Ios',
        'Yandex', '2020-05-15', '2022-05-12', false),
       (4, 'Test', 'Test test test', 'test test test test',  'Test', '2020-05-15', '2023-12-12', false);

INSERT INTO projects_statuses(project_id, statuses)
VALUES (1, 'CREATED'),
       (1, 'IN_PROGRESS'),
       (2, 'CREATED'),
       (2, 'IN_PROGRESS'),
       (3, 'CREATED'),
       (4, 'CREATED');

SELECT setval('projects_id_seq', (SELECT MAX(id) FROM projects));
