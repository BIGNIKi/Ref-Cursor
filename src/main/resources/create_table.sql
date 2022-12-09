DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts
(
    Id      INTEGER PRIMARY KEY generated always as identity,
    Name    VARCHAR(255),
    deleted BOOLEAN
);
insert into accounts (name, deleted)
values ('Shikhar', false);
insert into accounts (name, deleted)
values ('Jonathan', false);
insert into accounts (name, deleted)
values ('Kumara', false);
insert into accounts (name, deleted)
values ('Virat', false);
insert into accounts (name, deleted)
values ('Rohit', false);