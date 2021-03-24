
-- function to drop all tables and enums (no need to drop functions because they are create or replace)
do $$ declare
    r record;
begin
    for r in (select tablename from pg_tables where schemaname = current_schema())
        loop
execute 'DROP TABLE ' || quote_ident(r.tablename) || ' CASCADE';
end loop;
for r in (select distinct
                  t.typname as enum_name
              from pg_type t
                       join pg_enum e on t.oid = e.enumtypid
                       join pg_catalog.pg_namespace n ON n.oid = t.typnamespace)
        loop
execute 'DROP TYPE ' || quote_ident(r.enum_name);
end loop;

end
$$;

--start: testing essential tables
insert into brand (id, name, description, logo_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', null);

select *
from brand;

insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);

select *
from subject_to_mark;

insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);

select *
from actor;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'title', '40e6215d-b5c6-4896-987c-f30f3678f608',
        '40e6215d-b5c6-4896-987c-f30f3678f608', 4, true, 0, 0);

select *
from review;

delete
from review
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from actor
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from subject
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from brand
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';

--end: testing essential tables

--start: testing review triggers
insert into brand (id, name, description, logo_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', null);
insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);
insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);

select *
from subject;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'title', '40e6215d-b5c6-4896-987c-f30f3678f608',
        '40e6215d-b5c6-4896-987c-f30f3678f608', 4, true, 0, 0);

select *
from subject;

insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f603', 'title', '40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f608', 1, true, 0, 0);

select * from subject;

delete from review where id = '40e6215d-b5c6-4896-987c-f30f3678f608';

select * from subject;

delete from review where id = '40e6215d-b5c6-4896-987c-f30f3678f603';

select * from subject;

delete from actor where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from subject
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from brand
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
--end: testing review triggers


--start: testing review_vote triggers

insert into brand (id, name, description, logo_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', null);
insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);
insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);
insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f603', 'first_name', 'last_name', 'newemail', 'REVIEWER', 'password', now(),
        null, true);
insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'title', '40e6215d-b5c6-4896-987c-f30f3678f608',
        '40e6215d-b5c6-4896-987c-f30f3678f608', 4, true, 0, 0);

select *
from review;

insert into review_vote (review_id, reviewer_id, type)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f608', 'UP');

select * from review;

insert into review_vote (review_id, reviewer_id, type)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f603', 'DOWN');

select * from review;

delete from review_vote where review_id = '40e6215d-b5c6-4896-987c-f30f3678f608' and reviewer_id = '40e6215d-b5c6-4896-987c-f30f3678f608';

select * from review;

delete from review_vote where review_id = '40e6215d-b5c6-4896-987c-f30f3678f608' and reviewer_id = '40e6215d-b5c6-4896-987c-f30f3678f603';

select * from review;


delete from review where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from actor
where id = '40e6215d-b5c6-4896-987c-f30f3678f603';
delete
from actor
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from subject
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from brand
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
--end: testing review_vote triggers


--start: testing review_comment_vote triggers
insert into brand (id, name, description, logo_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', null);
insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);
insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'first_name', 'last_name', 'email', 'REVIEWER', 'password', now(), null,
        true);
insert into actor (id, first_name, last_name, email, role, password, created_timestamp, profile_image_id, is_active)
values ('40e6215d-b5c6-4896-987c-f30f3678f603', 'first_name', 'last_name', 'newemail', 'REVIEWER', 'password', now(),
        null, true);
insert into review (id, title, reviewer_id, subject_id, mark, is_shown, upvotes_count, downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'title', '40e6215d-b5c6-4896-987c-f30f3678f608',
        '40e6215d-b5c6-4896-987c-f30f3678f608', 4, true, 0, 0);
insert into review_comment (id, review_id, author_id, content, created_timestamp, is_shown, upvotes_count,
                            downvotes_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f608',
        '40e6215d-b5c6-4896-987c-f30f3678f608', 'content', now(), true, 0, 0);

select *
from review_comment;

insert into review_comment_vote (review_comment_id, reviewer_id, type)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f608', 'UP');

select * from review_comment;

insert into review_comment_vote (review_comment_id, reviewer_id, type)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f603', 'UP');

select * from review_comment;

delete from review_comment_vote where review_comment_id = '40e6215d-b5c6-4896-987c-f30f3678f608' and reviewer_id = '40e6215d-b5c6-4896-987c-f30f3678f608';

select * from review_comment;

delete from review_comment_vote where review_comment_id = '40e6215d-b5c6-4896-987c-f30f3678f608' and reviewer_id = '40e6215d-b5c6-4896-987c-f30f3678f603';

select * from review_comment;

delete from review_comment where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from review
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from actor
where id = '40e6215d-b5c6-4896-987c-f30f3678f603';
delete
from actor
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from subject
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete
from brand
where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
--end: testing review_comment_vote triggers

--start: testing subject triggers
insert into brand (id, name, description, logo_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', null);
insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);
insert into subject (id, name, description, brand_id, is_shown)
values ('40e6215d-b5c6-4896-987c-f30f3678f603', 'name', 'description', '40e6215d-b5c6-4896-987c-f30f3678f608', true);
insert into subject_tag (id, name, subjects_count)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', 'subject_tag_name', 0);

select *
from subject_tag;

insert into subject_to_tag (subject_id, tag_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f608', '40e6215d-b5c6-4896-987c-f30f3678f608');

select *
from subject_tag;

insert into subject_to_tag (subject_id, tag_id)
values ('40e6215d-b5c6-4896-987c-f30f3678f603','40e6215d-b5c6-4896-987c-f30f3678f608');

select * from subject_tag;

delete from subject_to_tag where subject_id = '40e6215d-b5c6-4896-987c-f30f3678f608' and tag_id = '40e6215d-b5c6-4896-987c-f30f3678f608';

select * from subject_tag;

delete from subject_to_tag where subject_id = '40e6215d-b5c6-4896-987c-f30f3678f603' and tag_id = '40e6215d-b5c6-4896-987c-f30f3678f608';

delete from subject_tag where id = '40e6215d-b5c6-4896-987c-f30f3678f603';
delete from subject where id = '40e6215d-b5c6-4896-987c-f30f3678f603';
delete from subject where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
delete from brand where id = '40e6215d-b5c6-4896-987c-f30f3678f608';
--end: testing subject triggers
