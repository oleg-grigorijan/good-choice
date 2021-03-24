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
    email             varchar(320) unique,
    role              actor_role  not null,
    password          varchar(60) not null,
    created_timestamp timestamp   not null,
    profile_image_id  uuid references image,
    is_active         boolean     not null
);

create table email_confirmation_token
(
    token             varchar(73) primary key,
    actor_id          uuid         not null references actor,
    email             varchar(320) not null,
    created_timestamp timestamp    not null
);

create table password_recovery_token
(
    token             varchar(73) primary key,
    actor_id          uuid      not null references actor,
    created_timestamp timestamp not null
);

create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description varchar     not null,
    logo_id     uuid references image
);

create table brand_presenter_details
(
    actor_id uuid primary key references actor,
    brand_id uuid not null references brand
);

create table brand_invitation_token
(
    token             varchar(73) primary key,
    brand_id          uuid         not null references brand,
    email             varchar(320) not null,
    created_timestamp timestamp    not null
);

create table subject
(
    id               uuid primary key,
    name             varchar(128) not null,
    description      varchar      not null,
    brand_id         uuid         not null references brand,
    is_shown         boolean      not null,
    primary_image_id uuid
);

create table subject_image
(
    image_id   uuid primary key references image,
    subject_id uuid not null references subject
);

alter table subject
    add foreign key (primary_image_id) references subject_image on delete set null;

create table subject_to_mark
(
    subject_id uuid references subject,
    mark       integer check ((mark >= 1) and (mark <= 5)),
    count      integer not null default 0 check (count >= 0),
    primary key (subject_id, mark)
);

create table subject_tag
(
    id             uuid primary key,
    name           varchar(128) not null,
    subjects_count integer not null default 0 check (subjects_count >= 0)
);

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
    reviewer_id     uuid         not null references actor,
    subject_id      uuid         not null references subject,
    mark            integer      not null check (mark >= 1 and mark <= 5),
    is_shown        boolean      not null,
    upvotes_count   integer      not null default 0 check (upvotes_count >= 0),
    downvotes_count integer      not null default 0 check (downvotes_count >= 0)
);

create table review_body
(
    id                uuid primary key,
    review_id         uuid      not null references review,
    content           varchar   not null,
    created_timestamp timestamp not null
);

create type review_point_type as enum ('ADVANTAGE', 'DISADVANTAGE');

create table review_point
(
    review_id uuid references review,
    ordering  int               not null check (ordering >= 0),
    type      review_point_type not null,
    content   varchar(256)      not null,
    primary key (review_id, ordering)
);
--todo: create index with select ordering


create table review_image
(
    image_id  uuid primary key references image,
    review_id uuid    not null references review,
    ordering  integer not null check (ordering >= 0)
);
--todo: create index with select ordering

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
    review_id         uuid      not null references review,
    author_id         uuid      not null references actor,
    content           varchar   not null,
    created_timestamp timestamp not null,
    is_shown          boolean   not null,
    upvotes_count     integer   not null default 0 check (upvotes_count >= 0),
    downvotes_count   integer   not null default 0 check (downvotes_count >= 0)
);

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
    id         uuid primary key,
    name       varchar(128) not null,
    definition varchar(256) not null,
    is_shown boolean not null
);

create table moderator_report
(
    id                      uuid primary key,
    issuer_id               uuid references actor,
    status                  report_status not null,
    assignee_moderator_id   uuid references actor,
    created_timestamp       timestamp     not null,
    last_modified_timestamp timestamp     not null
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
create function check_1_reviewer_1_subject_1_review_with_is_shown_true(actor uuid, subject uuid) returns void
as
$$
begin
    assert (select count(*)
            from review
            where subject_id = subject
              and reviewer_id = actor
              and is_shown = true) <= 1, '1 review from this reviewer to this subject is already shown';
end;
$$ language plpgsql;

create function review_trigger() returns trigger
as
$$
begin
    if (tg_op = 'INSERT') then
        update subject_to_mark
        set subject_to_mark.count = subject_to_mark.count + 1
        where subject_to_mark.subject_id = new.subject_id
          and subject_to_mark.mark = new.mark;

        perform check_1_reviewer_1_subject_1_review_with_is_shown_true(new.reviewer_id, new.subject_id);
        return new;

    elsif (tg_op = 'UPDATE') then
        if new.mark != old.mark then


            update subject_to_mark
            set subject_to_mark.count = subject_to_mark.count - 1
            where subject_to_mark.subject_id = old.subject_id
              and subject_to_mark.mark = old.mark;

            update subject_to_mark
            set subject_to_mark.count = subject_to_mark.count + 1
            where subject_to_mark.subject_id = new.subject_id
              and subject_to_mark.mark = new.mark;
        end if;

        perform check_1_reviewer_1_subject_1_review_with_is_shown_true(new.reviewer_id, new.subject_id);
        return new;

    elsif (tg_op = 'DELETE') then
        update subject_to_mark
        set subject_to_mark.count = subject_to_mark.count - 1
        where subject_to_mark.subject_id = old.subject_id
          and subject_to_mark.mark = old.mark;

        perform check_1_reviewer_1_subject_1_review_with_is_shown_true(old.reviewer_id, old.subject_id);
        return old;
    end if;

end;
$$ language plpgsql;

-- create function after_delete_review_trigger() returns trigger
-- as
-- $$
-- begin
--     perform change_subject_reviews_count(old.subject_id, -1);
--     perform update_subject_average_mark(old.subject_id);
--     return old;
-- end;
-- $$ language plpgsql;
--
-- create function after_update_review_trigger() returns trigger
-- as
-- $$
-- begin
--     perform update_subject_average_mark(new.subject_id);
--     perform check_1_reviewer_1_subject_1_review_with_is_shown_true(new.reviewer_id, new.subject_id);
--     return new;
-- end;
-- $$ language plpgsql;

create trigger after_insert_review_trigger
    after insert or update or delete
    on review
    for each row
execute procedure review_trigger();

-- create trigger after_delete_review_trigger
--     after delete
--     on review
--     for each row
-- execute procedure after_delete_review_trigger();
--
-- create trigger after_update_review_trigger
--     after update
--     on review
--     for each row
-- execute procedure after_update_review_trigger();


-- create function change_review_upvotes_count(updated_review_id uuid, upvotes_delta_count int) returns void
-- as
-- $$
-- begin
--     update review
--     set upvotes_count = upvotes_count + upvotes_delta_count
--     where id = updated_review_id;
-- end;
-- $$ language plpgsql;
--
--
-- create function change_review_downvotes_count(updated_review_id uuid, downvotes_delta_count int) returns void
-- as
-- $$
-- begin
--     update review
--     set downvotes_count = downvotes_count + downvotes_delta_count
--     where id = updated_review_id;
-- end;
-- $$ language plpgsql;

create function review_vote_trigger() returns trigger
as
$$
begin
    if (tg_op = 'INSERT') then
        if (new.type = 'UP') then
            update review set review.upvotes_count = review.upvotes_count + 1 where review.id = new.review_id;
        else
            update review set review.downvotes_count = review.downvotes_count + 1 where review.id = new.review_id;
        end if;
        return new;

    elsif (tg_op = 'UPDATE') then
        if (new.type != old.type) then
            if (old.type = 'UP') then
                update review
                set review.upvotes_count   = review.upvotes_count - 1,
                    review.downvotes_count = review.downvotes_count + 1
                where review.id = new.review_id;
            else
                update review
                set review.upvotes_count   = review.upvotes_count + 1,
                    review.downvotes_count = review.downvotes_count - 1
                where review.id = new.review_id;
            end if;
        end if;
        return new;

    elsif (tg_op = 'DELETE') then
        if (old.type = 'UP') then
            update review set review.upvotes_count = review.upvotes_count - 1 where review.id = old.review_id;
        else
            update review set review.downvotes_count = review.downvotes_count - 1 where review.id = old.review_id;
        end if;
        return old;
    end if;

end;
$$ language plpgsql;

-- create function after_delete_review_vote_trigger() returns trigger
-- as
-- $$
-- begin
--     if old.type = 'UP' then
--         perform change_review_upvotes_count(old.review_id, -1);
--     else
--         perform change_review_downvotes_count(old.review_id, -1);
--     end if;
--     return old;
-- end;
-- $$ language plpgsql;

create trigger review_vote_trigger
    after insert or update or delete
    on review_vote
    for each row
execute procedure review_vote_trigger();

-- create trigger after_delete_review_vote_trigger
--     after delete
--     on review_vote
--     for each row
-- execute procedure after_delete_review_vote_trigger();


-- create function change_review_comment_upvotes_count(updated_review_comment_id uuid, upvotes_delta_count int) returns void
-- as
-- $$
-- begin
--     update review_comment
--     set upvotes_count = upvotes_count + upvotes_delta_count
--     where id = updated_review_comment_id;
-- end;
-- $$ language plpgsql;
--
--
-- create function change_review_comment_downvotes_count(updated_review_comment_id uuid, downvotes_delta_count int) returns void
-- as
-- $$
-- begin
--     update review_comment
--     set downvotes_count = downvotes_count + downvotes_delta_count
--     where id = updated_review_comment_id;
-- end;
-- $$ language plpgsql;

create function review_comment_vote_trigger() returns trigger
as
$$
begin
    if (tg_op = 'INSERT') then
        if (new.type = 'UP') then
            update review_comment
            set review_comment.upvotes_count = review_comment.upvotes_count + 1
            where review_comment.id = new.review_comment_id;
        else
            update review_comment
            set review_comment.downvotes_count = review_comment.downvotes_count + 1
            where review_comment.id = new.review_comment_id;
        end if;
        return new;

    elsif (tg_op = 'UPDATE') then
        if (new.type != old.type) then
            if (old.type = 'UP') then
                update review_comment
                set review_comment.upvotes_count   = review_comment.upvotes_count - 1,
                    review_comment.downvotes_count = review_comment.downvotes_count + 1
                where review_comment.id = new.review_comment_id;
            else
                update review_comment
                set review_comment.upvotes_count   = review_comment.upvotes_count + 1,
                    review_comment.downvotes_count = review_comment.downvotes_count - 1
                where review_comment.id = new.review_comment_id;
            end if;
        end if;
        return new;

    elsif (tg_op = 'DELETE') then
        if (old.type = 'UP') then
            update review_comment
            set review_comment.upvotes_count = review_comment.upvotes_count - 1
            where review_comment.id = old.review_comment_id;
        else
            update review_comment
            set review_comment.downvotes_count = review_comment.downvotes_count - 1
            where review_comment.id = old.review_comment_id;
        end if;
        return old;
    end if;

end;
$$ language plpgsql;

-- create function after_delete_review_comment_vote_trigger() returns trigger
-- as
-- $$
-- begin
--     if old.type = 'UP' then
--         perform change_review_comment_upvotes_count(old.review_comment_id, -1);
--     else
--         perform change_review_comment_downvotes_count(old.review_comment_id, -1);
--     end if;
--     return old;
-- end;
-- $$ language plpgsql;

create trigger review_comment_vote_trigger
    after insert or update or delete
    on review_comment_vote
    for each row
execute procedure review_comment_vote_trigger();
--
-- create trigger after_delete_review_comment_vote_trigger
--     after delete
--     on review_comment_vote
--     for each row
-- execute procedure after_delete_review_comment_vote_trigger();


create function change_subject_tag_subjects_count(updated_subject_tag_id uuid, subjects_delta_count int) returns void
as
$$
begin
    update subject_tag
    set subjects_count = subjects_count + subjects_delta_count
    where id = updated_subject_tag_id;
end;
$$ language plpgsql;

create function subject_to_tag_trigger() returns trigger
as
$$
begin
    if (tg_op = 'INSERT') then
        update subject_tag
        set subjects_count = subjects_count + 1
        where id = new.tag_id;
        return new;

    elsif (tg_op = 'DELETE') then
        update subject_tag
        set subjects_count = subjects_count - 1
        where id = old.tag_id;
        return old;
    end if;

end;
$$ language plpgsql;
--
-- create function after_delete_subject_to_tag_trigger() returns trigger
-- as
-- $$
-- begin
--     perform change_subject_tag_subjects_count(old.tag_id, -1);
--     return old;
-- end;
-- $$ language plpgsql;

create trigger subject_to_tag_trigger
    after insert or delete
    on subject_to_tag
    for each row
execute procedure subject_to_tag_trigger();

-- create trigger after_delete_subject_to_tag_trigger
--     after delete
--     on subject_to_tag
--     for each row
-- execute procedure after_delete_subject_to_tag_trigger();

create function subject_trigger() returns trigger
as
$$
begin
    if (tg_op = 'INSERT') then
        insert into subject_to_mark (subject_id, mark, count)
        values (new.id, 1, 0),
               (new.id, 2, 0),
               (new.id, 3, 0),
               (new.id, 4, 0),
               (new.id, 5, 0);
        return new;

    elsif (tg_op = 'DELETE') then
        delete from subject_to_mark where subject_id = old.id;
        return old;
    end if;

end;
$$ language plpgsql;

create trigger subject_trigger
    after insert or update
    on subject
    for each row
execute procedure subject_trigger();
