INSERT INTO users (id, email, first_name, last_name, role)
VALUES (1, 'asd@gmail.com', 'Maksim', 'Stepanovich', 'EMPLOYEE'),
       (2, 'sdgfdsgsgr@mail.ru', 'Valera', 'Kuzminov', 'EMPLOYEE'),
       (3, 'ggnfdurnn@yandex.by','Alex', 'Newman', 'EMPLOYEE'),
       (4, 'qqweqttq@google.com', 'Michael', 'Harrington', 'MANAGER');

INSERT INTO projects_employees (project_id, employee_id)
VALUES (1, 1), (1,2), (2,3), (3,4);

INSERT INTO tasks_employees (task_id, employee_id)
VALUES (1, 1), (2,2), (6,3), (7,4);

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
