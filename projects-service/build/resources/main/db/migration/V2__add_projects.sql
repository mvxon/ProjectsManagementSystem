INSERT INTO projects (id, name, title, description, deleted, customer, creation_date, dead_line_date, status)
VALUES (1, 'ProjectX', 'Secret military project',
        'Project for military vehicles accounting, technologies to use: Java, ReactJs', false, 'USArmy',
        '2021-02-20', '2023-02-23', 'CREATED'),
       (2, 'EShop', 'Online clothes shop',
        'Online clothes shop with opportunity to order clothes, technologies to use: Python, .NET, PHP', false,
        'MFashion', '2022-05-15', '2023-03-12', 'CREATED'),
       (3, 'Yandex Taxi', 'Taxi application project',
        'Taxi application with ability to order taxi online. Technologies to use: Ruby, Java, Android, Ios', false,
        'Yandex', '2020-05-15', '2022-05-12', 'CREATED'),
       (4, 'Test', 'Test test test', 'test test test test', true, 'Test', '2020-05-15', '2023-12-12', 'CREATED');

SELECT setval('projects_id_seq', (SELECT MAX(id) FROM projects));