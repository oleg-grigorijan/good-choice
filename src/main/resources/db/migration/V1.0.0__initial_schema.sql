create type actor_role as enum ('REVIEWER', 'HR', 'MODERATOR', 'ADMINISTRATOR', 'BRAND_PRESENTER', 'SYSTEM');

create table image
(
    id       uuid primary key,
    location varchar(256) not null
);

create table actor
(
    id                uuid primary key,
    first_name        varchar(64) not null,
    last_name         varchar(64) not null,
    email             varchar(320) null unique,
    role              actor_role not null,
    password_hash     varchar(60) not null,
    profile_image_id  uuid null references image on delete set null,
    is_active         boolean not null,
    created_timestamp timestamp not null
);

create table email_confirmation_token
(
    token             varchar(73) primary key,
    actor_id          uuid not null references actor,
    email             varchar(320) not null,
    created_timestamp timestamp not null
);

create table password_recovery_token
(
    token             varchar(73) primary key,
    actor_id          uuid not null references actor,
    created_timestamp timestamp not null
);

create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description varchar not null,
    is_active   boolean not null,
    logo_id     uuid null references image on delete set null
);

create table brand_presenter_details
(
    actor_id uuid primary key references actor,
    brand_id uuid not null references brand
);

create table brand_presenter_invitation_token
(
    token             varchar(73) primary key,
    brand_id          uuid not null references brand,
    email             varchar(320) not null,
    created_timestamp timestamp not null
);

create table employee_invitation_token
(
    token             varchar(73) primary key,
    first_name        varchar(64) not null,
    last_name         varchar(64) not null,
    email             varchar(320) not null,
    role              actor_role not null check (role in ('HR', 'MODERATOR', 'ADMINISTRATOR')),
    created_timestamp timestamp not null
);

create table subject
(
    id                uuid primary key,
    name              varchar(128) not null,
    description       varchar not null,
    brand_id          uuid not null references brand,
    primary_image_id  uuid null,
    is_shown          boolean not null,
    created_timestamp timestamp not null
);

create table subject_image
(
    image_id   uuid references image on delete cascade,
    subject_id uuid not null references subject,
    primary key (subject_id, image_id)
);

alter table subject
    add foreign key (id, primary_image_id) references subject_image (subject_id, image_id);

create table subject_to_mark
(
    subject_id uuid references subject,
    mark       integer check ((mark >= 1) and (mark <= 5)),
    count      integer not null default 0 check (count >= 0),
    primary key (subject_id, mark)
);
comment on table subject_to_mark is 'Caching table';
comment on column subject_to_mark.count is 'Caching column';

create table subject_tag
(
    id             uuid primary key,
    name           varchar(64) not null,
    subjects_count integer not null default 0 check (subjects_count >= 0)
);
comment on column subject_tag.subjects_count is 'Caching column';

create table subject_to_tag
(
    subject_id uuid references subject,
    tag_id     uuid references subject_tag on delete cascade,
    primary key (subject_id, tag_id)
);

create table review
(
    id              uuid primary key,
    title           varchar(128) not null,
    reviewer_id     uuid not null references actor,
    subject_id      uuid not null references subject,
    mark            integer not null check (mark >= 1 and mark <= 5),
    is_shown        boolean not null,
    upvotes_count   integer not null default 0 check (upvotes_count >= 0),
    downvotes_count integer not null default 0 check (downvotes_count >= 0)
);
comment on column review.upvotes_count is 'Caching column';
comment on column review.downvotes_count is 'Caching column';

create table review_body
(
    id                uuid primary key,
    review_id         uuid not null references review,
    content           varchar not null,
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

create table review_image
(
    image_id  uuid references image on delete cascade,
    review_id uuid not null references review,
    ordering  integer not null,
    primary key (review_id, image_id)
);

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
    is_shown          boolean not null,
    created_timestamp timestamp not null,
    upvotes_count     integer not null default 0 check (upvotes_count >= 0),
    downvotes_count   integer not null default 0 check (downvotes_count >= 0)
);
comment on column review_comment.upvotes_count is 'Caching column';
comment on column review_comment.downvotes_count is 'Caching column';

create table review_comment_vote
(
    review_comment_id uuid references review_comment,
    reviewer_id       uuid references actor,
    type              vote_type not null,
    primary key (review_comment_id, reviewer_id)
);

create type report_status as enum ('OPEN', 'IN_PROGRESS', 'CLOSED');

create table moderator_report_reason
(
    id            uuid primary key,
    name          varchar(64) not null,
    definition    varchar(256) not null,
    is_deprecated boolean not null
);

create table moderator_report
(
    id                      uuid primary key,
    issuer_id               uuid null references actor,
    status                  report_status not null,
    assignee_moderator_id   uuid null references actor,
    created_timestamp       timestamp not null,
    last_modified_timestamp timestamp not null
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
    report_id         uuid references moderator_report,
    review_comment_id uuid references review_comment,
    primary key (report_id, review_comment_id)
);


create or replace function check_1_reviewer_1_subject_1_review_with_is_shown_true(actor uuid, subject uuid) returns void as
$$
begin
    assert (select count(*)
            from review
            where subject_id = subject
              and reviewer_id = actor
              and is_shown = true
           ) <= 1, '1 review from this reviewer to this subject is already shown';
end;
$$ language plpgsql;

create or replace function review_trigger() returns trigger as
$$
begin
    perform check_1_reviewer_1_subject_1_review_with_is_shown_true(new.reviewer_id, new.subject_id);

    if (not (old is null) and old.is_shown) then
        update subject_to_mark
        set count = count - 1
        where subject_to_mark.subject_id = old.subject_id
          and subject_to_mark.mark = old.mark;
    end if;

    if (not (new is null) and new.is_shown) then
        update subject_to_mark
        set count = count + 1
        where subject_to_mark.subject_id = new.subject_id
          and subject_to_mark.mark = new.mark;
    end if;

    return null;
end;
$$ language plpgsql;

create trigger after_insert_review_trigger
    after insert or update of subject_id, mark, is_shown or delete
    on review
    for each row
execute procedure review_trigger();


create or replace function review_vote_trigger() returns trigger as
$$
begin
    if (not (old is null)) then
        update review
        set upvotes_count   = case old.type
                                  when 'UP' then upvotes_count - 1
                                  when 'DOWN' then upvotes_count end,
            downvotes_count = case old.type
                                  when 'UP' then downvotes_count
                                  when 'DOWN' then downvotes_count - 1 end
        where review.id = old.review_id;
    end if;

    if (not (new is null)) then
        update review
        set upvotes_count   = case new.type
                                  when 'UP' then upvotes_count + 1
                                  when 'DOWN' then upvotes_count end,
            downvotes_count = case new.type
                                  when 'UP' then downvotes_count
                                  when 'DOWN' then downvotes_count + 1 end
        where review.id = new.review_id;
    end if;

    return null;
end;
$$ language plpgsql;

create trigger review_vote_trigger
    after insert or update of review_id, type or delete
    on review_vote
    for each row
execute procedure review_vote_trigger();


create or replace function review_comment_vote_trigger() returns trigger as
$$
begin
    if (not (old is null)) then
        update review_comment
        set upvotes_count   = case old.type
                                  when 'UP' then upvotes_count - 1
                                  when 'DOWN' then upvotes_count end,
            downvotes_count = case old.type
                                  when 'UP' then downvotes_count
                                  when 'DOWN' then downvotes_count - 1 end
        where review_comment.id = old.review_comment_id;
    end if;

    if (not (new is null)) then
        update review_comment
        set upvotes_count   = case new.type
                                  when 'UP' then upvotes_count + 1
                                  when 'DOWN' then upvotes_count end,
            downvotes_count = case new.type
                                  when 'UP' then downvotes_count
                                  when 'DOWN' then downvotes_count + 1 end
        where review_comment.id = new.review_comment_id;
    end if;

    return null;
end;
$$ language plpgsql;

create trigger review_comment_vote_trigger
    after insert or update of review_comment_id, type or delete
    on review_comment_vote
    for each row
execute procedure review_comment_vote_trigger();


create or replace function subject_to_tag_trigger() returns trigger as
$$
begin
    if (not (old is null) and (select is_shown from subject where subject.id = old.subject_id)) then
        update subject_tag
        set subjects_count = subjects_count - 1
        where id = old.tag_id;
    end if;

    if (not (new is null) and (select is_shown from subject where subject.id = new.subject_id)) then
        update subject_tag
        set subjects_count = subjects_count + 1
        where id = new.tag_id;
    end if;

    return null;
end;
$$ language plpgsql;

create trigger subject_to_tag_trigger
    after insert or update or delete
    on subject_to_tag
    for each row
execute procedure subject_to_tag_trigger();


create or replace function subject_trigger() returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into subject_to_mark (subject_id, mark)
        values (new.id, 1),
               (new.id, 2),
               (new.id, 3),
               (new.id, 4),
               (new.id, 5);

        return null;
    end if;

    if (old.is_shown = new.is_shown) then
        return null;
    end if;

    ----------

    if (not (old is null) and old.is_shown) then
        update subject_tag
        set subjects_count = subjects_count - 1
        where id in (select tag_id from subject_to_tag where subject_id = old.id);
    end if;

    if (not (new is null) and new.is_shown) then
        update subject_tag
        set subjects_count = subjects_count + 1
        where id in (select tag_id from subject_to_tag where subject_id = new.id);
    end if;

    return null;
end;
$$ language plpgsql;

create trigger subject_trigger
    after insert or update of is_shown or delete
    on subject
    for each row
execute procedure subject_trigger();
