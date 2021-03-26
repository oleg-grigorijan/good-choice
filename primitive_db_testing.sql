-- function to drop all tables and enums (no need to drop functions because they are create or replace)
do
$$
    declare
        r record;
    begin
        for r in (select tablename from pg_tables where schemaname = current_schema())
            loop
                execute 'DROP TABLE ' || quote_ident(r.tablename) || ' CASCADE';
            end loop;
        for r in (select distinct t.typname as enum_name
                  from pg_type t
                           join pg_enum e on t.oid = e.enumtypid
                           join pg_catalog.pg_namespace n ON n.oid = t.typnamespace)
            loop
                execute 'DROP TYPE ' || quote_ident(r.enum_name);
            end loop;
    end
$$;

--start: testing essential tables
insert into brand (id, name, description, logo_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', null, true);

select *
from brand;

insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());

select *
from subject_to_mark;

insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);

select *
from actor;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('a0000000-0000-0000-0000-000000000000', 'title', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 4, true, 0, 0);

select *
from review;

delete
from review
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from actor
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject_to_mark
where subject_id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from brand
where id = 'a0000000-0000-0000-0000-000000000000';

--end: testing essential tables

--start: testing review triggers
insert into brand (id, name, description, logo_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', null, true);
insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());
insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null, true);

select *
from subject;

select *
from subject_to_mark;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('a0000000-0000-0000-0000-000000000000', 'title', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 4, true, 0, 0);

select *
from subject;
select *
from review;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('b0000000-0000-0000-0000-000000000000', 'title', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 1, false, 0, 0);

select *
from review;


--must be error here
update review
set is_shown = true
where id = 'b0000000-0000-0000-0000-000000000000';

select *
from subject;
select *
from subject_to_mark
order by subject_id, mark;

update review
set mark = 1
where id = 'a0000000-0000-0000-0000-000000000000';

select *
from subject_to_mark
order by subject_id, mark;

update review
set is_shown = false
where id = 'a0000000-0000-0000-0000-000000000000';

select *
from subject_to_mark
order by subject_id, mark;

delete
from review
where id = 'a0000000-0000-0000-0000-000000000000';

select *
from subject;

delete
from review
where id = 'b0000000-0000-0000-0000-000000000000';

select *
from subject;

delete
from actor
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject_to_mark
where subject_id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from brand
where id = 'a0000000-0000-0000-0000-000000000000';
--end: testing review triggers


--start: testing review_vote triggers

insert into brand (id, name, description, logo_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', null, true);
insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());
insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);
insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('b0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'newemail', 'REVIEWER', 'password', now(),
        null, true);
insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('a0000000-0000-0000-0000-000000000000', 'title', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 4, true, 0, 0);

select *
from review;

insert into review_vote (review_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'a0000000-0000-0000-0000-000000000000', 'UP');

select *
from review;

insert into review_vote (review_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'b0000000-0000-0000-0000-000000000000', 'UP');

select *
from review;

insert into review_vote (review_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'b0000000-0000-0000-0000-000000000000', 'DOWN')
on conflict (review_id, reviewer_id) do update set type = 'DOWN';

delete
from review_vote
where review_id = 'a0000000-0000-0000-0000-000000000000'
  and reviewer_id = 'a0000000-0000-0000-0000-000000000000';

select *
from review;

delete
from review_vote
where review_id = 'a0000000-0000-0000-0000-000000000000'
  and reviewer_id = 'b0000000-0000-0000-0000-000000000000';

select *
from review;


delete
from review
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from actor
where id = 'b0000000-0000-0000-0000-000000000000';
delete
from actor
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject_to_mark
where subject_id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from brand
where id = 'a0000000-0000-0000-0000-000000000000';
--end: testing review_vote triggers


--start: testing review_comment_vote triggers
insert into brand (id, name, description, logo_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', null, true);
insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());
insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);
insert into actor (id, first_name, last_name, email, role, password_hash, created_timestamp, profile_image_id, is_active)
values ('b0000000-0000-0000-0000-000000000000', 'first_name', 'last_name', 'newemail', 'REVIEWER', 'password', now(),
        null, true);
insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('a0000000-0000-0000-0000-000000000000', 'title', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 4, true, 0, 0);
insert into review_comment (id, review_id, author_id, content, created_timestamp, is_shown, upvotes_count,
                            downvotes_count)
values ('a0000000-0000-0000-0000-000000000000', 'a0000000-0000-0000-0000-000000000000',
        'a0000000-0000-0000-0000-000000000000', 'content', now(), true, 0, 0);

select *
from review_comment;

insert into review_comment_vote (review_comment_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'a0000000-0000-0000-0000-000000000000', 'UP');

select *
from review_comment;

insert into review_comment_vote (review_comment_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'b0000000-0000-0000-0000-000000000000', 'UP');

select *
from review_comment;

insert into review_comment_vote (review_comment_id, reviewer_id, type)
values ('a0000000-0000-0000-0000-000000000000', 'b0000000-0000-0000-0000-000000000000', 'DOWN')
on conflict (review_comment_id, reviewer_id) do update set type = 'DOWN';

delete
from review_comment_vote
where review_comment_id = 'a0000000-0000-0000-0000-000000000000'
  and reviewer_id = 'a0000000-0000-0000-0000-000000000000';

select *
from review_comment;

delete
from review_comment_vote
where review_comment_id = 'a0000000-0000-0000-0000-000000000000'
  and reviewer_id = 'b0000000-0000-0000-0000-000000000000';

select *
from review_comment;

delete
from review_comment
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from review
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from actor
where id = 'b0000000-0000-0000-0000-000000000000';
delete
from actor
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject_to_mark
where subject_id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from brand
where id = 'a0000000-0000-0000-0000-000000000000';
--end: testing review_comment_vote triggers

--start: testing subject triggers
insert into brand (id, name, description, logo_id, is_active)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', null, true);
insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('a0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());
insert into subject (id, name, description, brand_id, is_shown, created_timestamp)
values ('b0000000-0000-0000-0000-000000000000', 'name', 'description', 'a0000000-0000-0000-0000-000000000000', true, now());
insert into subject_tag (id, name, subjects_count)
values ('a0000000-0000-0000-0000-000000000000', 'subject_tag_name', 0);

select *
from subject_tag;

insert into subject_to_tag (subject_id, tag_id)
values ('a0000000-0000-0000-0000-000000000000', 'a0000000-0000-0000-0000-000000000000');

select *
from subject_tag;

insert into subject_to_tag (subject_id, tag_id)
values ('b0000000-0000-0000-0000-000000000000', 'a0000000-0000-0000-0000-000000000000');

select *
from subject_tag;

delete
from subject_to_tag
where subject_id = 'a0000000-0000-0000-0000-000000000000'
  and tag_id = 'a0000000-0000-0000-0000-000000000000';

select *
from subject_tag;

update subject
set is_shown = false
where id = 'a0000000-0000-0000-0000-000000000000';

select *
from subject_tag;

delete
from subject_to_tag
where subject_id = 'b0000000-0000-0000-0000-000000000000'
  and tag_id = 'a0000000-0000-0000-0000-000000000000';

delete
from subject_tag
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from subject_to_mark
where subject_id = 'a0000000-0000-0000-0000-000000000000'
   or subject_id = 'b0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'b0000000-0000-0000-0000-000000000000';
delete
from subject
where id = 'a0000000-0000-0000-0000-000000000000';
delete
from brand
where id = 'a0000000-0000-0000-0000-000000000000';
--end: testing subject triggers
