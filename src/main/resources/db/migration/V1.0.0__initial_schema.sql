-- TODO: Think about image storing and referencing
-- TODO: Moderator feedback
-- TODO?: Email tasks

create type actor_role as enum ('REVIEWER', 'HR', 'MODERATOR', 'ADMINISTRATOR', 'BRAND_PRESENTER');

create table image(
    id uuid primary key,
    location varchar not null
);

create table actor
(
    id                 uuid primary key,
    first_name         varchar(64) not null,
    last_name          varchar(64) not null,
    email              varchar(320) unique, -- TODO: Email verification on registration, on update
    role               actor_role not null,
    password           varchar(60) not null,
    created_timestamp  timestamp not null,
    profile_image_id   uuid references image,
    is_active          boolean not null
);

create table email_confirmation_token
(
    token             varchar(73) primary key,
    actor_id          uuid not null references actor,
    email             varchar(320) not null unique,
    created_timestamp timestamp not null
);
create index email_confirmation_token_created_timestamp_idx ON email_confirmation_token (created_timestamp);

create table password_recovery_token
(
    token             varchar(73) primary key,
    actor_id          uuid not null references actor,
    created_timestamp timestamp not null
);
create index password_recovery_token_created_timestamp_idx ON password_recovery_token (created_timestamp);


create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description text,
    logo_id     uuid references image
    --TODO?: subjects_count integer not null           -- TODO: Trigger
);

create table brand_presenter_details
(
    actor_id uuid primary key references actor,
    brand_id uuid not null references brand
);

create table brand_invitation_token
(
    token             varchar(73) primary key,
    brand_id          uuid not null references brand,
    email             varchar(320) not null,
    created_timestamp timestamp not null
);
create index brand_invitation_token_created_timestamp_idx ON brand_invitation_token (created_timestamp);


-- TODO?: Subject request
create table subject
(
    id            uuid primary key,
    name          varchar(128) not null,
    description   varchar,
    brand_id      uuid not null references brand,
    is_shown      boolean not null,
    --TODO: primary_image_id uuid references subject_image,
    reviews_count integer not null,
    average_mark  float
--  TODO:   constraint average_mark_check check ((average_mark IS NULL) = (reviews_count = 0))
);

create table subject_image
(
    id uuid primary key,
    image_id uuid not null references image,
    subject_id uuid not null references subject
);


create table subject_to_mark
(
    subject_id uuid references subject,
    mark integer,
    count integer not null,
    primary key (subject_id, mark)
);

create table subject_tag
(
    id   uuid primary key,
    name varchar(128) not null,
    subjects_count integer not null
);

create table subject_to_tag
(
    subject_id uuid references subject,
    tag_id     uuid references subject_tag on delete cascade,
    primary key (subject_id, tag_id)
);

create table review
(
    id             uuid primary key,
    title          varchar(128),
    reviewer_id    uuid not null references actor,
    subject_id     uuid not null references subject, -- TODO?: Subjects created by reviewer
    mark           integer not null,
    is_shown       boolean not null,
    upvotes_count   integer not null,
    downvotes_count integer not null,
    constraint review_mark_range_check check (mark >= 1 and mark <= 5)
);
--TODO?: index by review_body_created_timestamp

create table review_body
(
    id                uuid primary key,
    review_id         uuid not null references review,
    content           varchar not null,
    --TODO: last_modified_timestamp timestamp,
    created_timestamp timestamp not null
);

create type review_point_type as enum ('ADVANTAGE', 'DISADVANTAGE');

create table review_point
(
    review_id uuid references review,
    ordering  int not null,
    type      review_point_type not null,
    content   varchar(256) not null,
    primary key (review_id, ordering)
);
-- no sense in creating index by ordering because it does not guarantee select order

create table review_image
(
    id        uuid primary key,
    review_id uuid not null references review,
    ordering  integer not null
);
-- no sense in creating index by ordering because it does not guarantee select order

create type vote_type as enum ('UP', 'DOWN');

create table review_vote
(
    review_id   uuid references review,
    reviewer_id uuid references actor,
    type        vote_type not null,
    primary key (review_id, reviewer_id)
);

create table review_comment
(
    id                uuid primary key,
    review_id         uuid not null references review,
    author_id         uuid not null references actor,
    content           varchar not null,
    created_timestamp timestamp not null,
    --TODO: last_modified_timestamp timestamp,
    is_shown          boolean not null,
    upvotes_count      integer not null,
    downvotes_count    integer not null
);

create table review_comment_vote
(
    review_comment_id uuid references review_comment,
    reviewer_id       uuid references actor,
    type              vote_type not null,
    primary key (review_comment_id, reviewer_id)
);

create type report_issuer_type as enum ('GUEST', 'REVIEWER', 'BRAND_OWNER', 'SYSTEM');

create type report_status as enum ('OPEN', 'IN_PROGRESS', 'CLOSED');

create table moderator_report_reason
(
    id         uuid primary key,
    name       varchar(128) not null,
    definition varchar(256) not null,
    is_shown   boolean not null
);

create table moderator_report
(
    id                      uuid primary key,
    issuer_id               uuid references actor,
    status                  report_status not null,
    assignee_moderator_id   uuid references actor,
    created_timestamp       timestamp not null,
    last_modified_timestamp timestamp
);
create index moderator_report_created_timestamp_idx ON moderator_report (created_timestamp);


create table moderator_report_to_reason
(
    report_id uuid references moderator_report,
    reason_id uuid references moderator_report_reason,
    primary key (report_id, reason_id)
);

create table moderator_report_to_review
(
    report_id uuid references moderator_report,
    review_id uuid references review,
    primary key (report_id, review_id)
);

create table moderator_report_to_review_comment
(
    report_id uuid references moderator_report,
    review_comment_id uuid references review_comment,
    primary key (report_id, review_comment_id)
);

create table deleted_by_moderator_report
(
    id  uuid primary key,
    reason_id uuid not null references moderator_report_reason,
    report_id uuid not null references moderator_report,
    note varchar
);

create type email_task_type as enum ('SEND_EMAIL');

create table email_task
(
    id uuid primary key,
    type email_task_type not null,
    params varchar,
    created_timestamp timestamp not null
);
create index email_task_created_timestamp_idx ON email_task (created_timestamp);



create or replace function change_subject_reviews_count(updated_subject_id uuid, reviews_delta_count int) returns void
as $$
    begin
        update subject
        set reviews_count = reviews_count + reviews_delta_count
            where id = updated_subject_id;
    end;
$$ language plpgsql;

create or replace function update_subject_average_mark(updated_subject_id uuid) returns void
as $$
    begin
        update subject
        set average_mark = (select AVG(mark) from review)
            where id = updated_subject_id;
    end;
$$ language plpgsql;

create or replace function after_insert_review_trigger() returns trigger
as $after_insert_review_trigger$
begin
    perform change_subject_reviews_count(new.subject_id, 1);
    perform update_subject_average_mark(new.subject_id);
    return old;
end;
$after_insert_review_trigger$ language plpgsql;

create or replace function after_delete_review_trigger() returns trigger
as $after_delete_review_trigger$
begin
    perform change_subject_reviews_count(old.subject_id, -1);
    perform update_subject_average_mark(old.subject_id);
    return old;
end;
$after_delete_review_trigger$ language plpgsql;

create or replace function after_update_review_trigger() returns trigger
as $after_delete_review_trigger$
begin
    perform update_subject_average_mark(new.subject_id);
    return new;
end;
$after_delete_review_trigger$ language plpgsql;

create trigger after_insert_review_trigger after insert on review
    for each row execute procedure after_insert_review_trigger();

create trigger after_delete_review_trigger after delete on review
    for each row execute procedure after_delete_review_trigger();

create trigger after_update_review_trigger after update on review
    for each row execute procedure after_update_review_trigger();




create or replace function change_review_upvotes_count (updated_review_id uuid, upvotes_delta_count int) returns void
as $$
    begin
        update review
        set upvotes_count = upvotes_count + upvotes_delta_count
            where id = updated_review_id;
    end;
$$ language plpgsql;


create or replace function change_review_downvotes_count (updated_review_id uuid, downvotes_delta_count int) returns void
as $$
begin
    update review
    set downvotes_count = downvotes_count + downvotes_delta_count
        where id = updated_review_id;
end;
$$ language plpgsql;

create or replace function after_insert_review_vote_trigger() returns trigger
as $after_insert_review_vote_trigger$
begin
    if new.type = 'UP' then
        perform change_review_upvotes_count(new.review_id, 1);
    else
        perform change_review_downvotes_count(new.review_id, 1);
    end if;
    return new;
end;
$after_insert_review_vote_trigger$ language plpgsql;

create or replace function after_delete_review_vote_trigger() returns trigger
as $after_delete_review_vote_trigger$
begin
    if old.type = 'UP' then
        perform change_review_upvotes_count(old.review_id, -1);
    else
        perform change_review_downvotes_count(old.review_id, -1);
    end if;
    return old;
end;
$after_delete_review_vote_trigger$ language plpgsql;

create trigger after_insert_review_vote_trigger after insert on review_vote
    for each row execute procedure after_insert_review_vote_trigger();

create trigger after_delete_review_vote_trigger after delete on review_vote
    for each row execute procedure after_delete_review_vote_trigger();



create or replace function change_review_comment_upvotes_count (updated_review_comment_id uuid, upvotes_delta_count int) returns void
as $$
begin
    update review_comment
    set upvotes_count = upvotes_count + upvotes_delta_count
    where id = updated_review_comment_id;
end;
$$ language plpgsql;


create or replace function change_review_comment_downvotes_count (updated_review_comment_id uuid, downvotes_delta_count int) returns void
as $$
begin
    update review_comment
    set downvotes_count = downvotes_count + downvotes_delta_count
    where id = updated_review_comment_id;
end;
$$ language plpgsql;

create or replace function after_insert_review_comment_vote_trigger() returns trigger
as $after_insert_review_comment_vote_trigger$
begin
    if new.type = 'UP' then
        perform change_review_comment_upvotes_count(new.review_comment_id, 1);
    else
        perform change_review_comment_downvotes_count(new.review_comment_id, 1);
    end if;
    return new;
end;
$after_insert_review_comment_vote_trigger$ language plpgsql;

create or replace function after_delete_review_comment_vote_trigger() returns trigger
as $after_delete_review_comment_vote_trigger$
begin
    if old.type = 'UP' then
        perform change_review_comment_upvotes_count(old.review_comment_id, -1);
    else
        perform change_review_comment_downvotes_count(old.review_comment_id, -1);
    end if;
    return old;
end;
$after_delete_review_comment_vote_trigger$ language plpgsql;

create trigger after_insert_review_comment_vote_trigger after insert on review_comment_vote
    for each row execute procedure after_insert_review_comment_vote_trigger();

create trigger after_delete_review_comment_vote_trigger after delete on review_comment_vote
    for each row execute procedure after_delete_review_comment_vote_trigger();


create or replace function change_subject_tag_subjects_count(updated_subject_tag_id uuid, subjects_delta_count int) returns void
as $$
begin
    update subject_tag
    set subjects_count = subjects_count + subjects_delta_count
    where id = updated_subject_tag_id;
end;
$$ language plpgsql;

create or replace function after_insert_subject_to_tag_trigger() returns trigger
as $after_insert_subject_to_tag_trigger$
begin
    perform change_subject_tag_subjects_count(new.tag_id, 1);
    return new;
end;
$after_insert_subject_to_tag_trigger$ language plpgsql;

create or replace function after_delete_subject_to_tag_trigger() returns trigger
as $after_delete_review_comment_vote_trigger$
begin
    perform change_subject_tag_subjects_count(old.tag_id, -1);
    return old;
end;
$after_delete_review_comment_vote_trigger$ language plpgsql;

create trigger after_insert_subject_to_tag_trigger after insert on subject_to_tag
    for each row execute procedure after_insert_subject_to_tag_trigger();

create trigger after_delete_subject_to_tag_trigger after delete on subject_to_tag
    for each row execute procedure after_delete_subject_to_tag_trigger();