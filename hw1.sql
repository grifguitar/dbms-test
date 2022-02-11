-- NOTE: sql полностью регистронезависимый

-- # создание базы данных:
-- >>> create database ctd;

-- # перейти в консоль базы данных:
-- >>> use ctd;
-- NOTE: не поддерживается в postgres, нужно переключится ручками

-- # создание таблиц:

create table groups
(
    group_id   int,
    group_name char(6)
);

create table students
(
    student_id   int,
    group_id     int,
    student_name varchar(1000)
);

-- # char и varchar:

-- NOTE: тип "char" неявно заполняется пробелами до указанной длины
-- NOTE: "varchar" хранит свою настоящую длину

-- # вставка данных:

insert into groups(group_name, group_id)
values ('М34391', 1),
       ('М34381', 2);

insert into students(student_id, group_id, student_name)
values (1, 1, 'Dmitry Gnatyuk'),
       (2, 1, 'Artemy Kononov'),
       (3, 2, 'Andrey Bocharnikov');

-- # получение данных:

select group_id, group_name
from groups;

select group_name, group_id
from groups;

select group_name
from groups;

select *
from groups;

select *
from students;

-- NOTE: при "select *" порядок столбцов берется берется из определения таблицы

-- # natural join: (естественное соединение)

select student_name, group_name
from students
         natural join groups;

-- NOTE: "natural join" выполняет соединение по одинаковым значениям одноименных столбцов
-- NOTE: это можно написать в явном виде, как:

-- # inner join: (внутренние соединение по условию)

select student_name, group_name
from students
         inner join groups g
                    on students.group_id = g.group_id;

-- NOTE: результат будет тот же самый

-- NOTE: как работает join?
-- NOTE: он рассматривает все пары строк
-- NOTE: {x, y} | for all x in X, y in Y
-- NOTE: если для пары строк условие выполняется, то это пара идет в ответ

-- # указание имени таблицы, при указании столбца:

select *
from students
         inner join groups g
                    on students.group_id <= g.group_id;

-- NOTE: будет несколько различных столбцов group_id
-- NOTE: правильно всегда указывать из какой таблички, при неоднозначности

select student_name, group_name, students.group_id
from students
         inner join groups g
                    on students.group_id <= g.group_id;

-- # повторяющийся идентификатор

insert into groups(group_id, group_name)
values (1, 'М34371');

select student_name, group_name
from students
         natural join groups;

-- NOTE: некоторые студенты появились в нескольких группах
-- NOTE: нужно сделать уникальные идентификаторы

-- # удаление и запрет повторяющегося идентификатора:

-- NOTE: удаление

delete
from groups
where group_name = 'М34371';

-- NOTE: переименование

update groups
set group_name = 'М34342'
where group_id = 1;

alter table groups
    add constraint group_id_unique
        unique (group_id);

-- ERROR:

-- insert into groups(group_id, group_name)
-- values (1, 'М34371');

-- NOTE: теперь не сможем так делать, потому что есть ограничение

-- # несуществующий идентификатор:

insert into students (student_name, group_id, student_id)
values ('Vladimir Vladimirovich', 3, 4);

select *
from students
         natural join groups;

-- NOTE: не видим студента 'Vladimir Vladimirovich'

-- # удаление и запрет несуществующего идентификатора:

update students
set group_id = 1
where student_id = 4;

-- NOTE: поместим студента 'Vladimir Vladimirovich' в группу с group_id = 1

-- ATTENTION: по стандарту sql, часть "where" в "update" или "delete" не является обязательной
-- ATTENTION: если она не указана, то применяется ко всем строчкам

alter table students
    add constraint students_group_id_f_key foreign key
        (group_id) references groups (group_id);

select *
from students
         natural join groups;

-- ERROR:

-- update students
-- set group_id = 10
-- where student_id = 4;

-- NOTE: теперь не сможем так делать, потому что есть ограничение

-- # удаление таблиц:
-- >>> drop table groups cascade;
-- >>> drop table students cascade;
