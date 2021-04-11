-- We have no records in employee_invitation_token table, so we can drop it
drop table employee_invitation_token;

create table employee_invitation
(
    id                       uuid primary key,
    token                    varchar(73) unique not null,
    email                    varchar(320) unique not null,
    role                     actor_role not null check (role in ('HR', 'MODERATOR', 'ADMINISTRATOR')),
    suggested_first_name     varchar(64) not null,
    suggested_last_name      varchar(64) not null,
    created_timestamp        timestamp not null,
    last_refreshed_timestamp timestamp not null,
    expired_timestamp        timestamp not null
);
