create user "good-choice-user" password 'good-choice-pass';
create database "good-choice";
create database "good-choice-test";

grant connect on database "good-choice-test" to "good-choice-user";
grant connect on database "good-choice" to "good-choice-user";

\c "good-choice"

grant all privileges on all tables in schema public to "good-choice-user";
grant all privileges on all sequences in schema public to "good-choice-user";
grant all privileges on all functions in schema public to "good-choice-user";

create extension if not exists "uuid-ossp";

\c "good-choice-test"

grant all privileges on all tables in schema public to "good-choice-user";
grant all privileges on all sequences in schema public to "good-choice-user";
grant all privileges on all functions in schema public to "good-choice-user";

create extension if not exists "uuid-ossp";
