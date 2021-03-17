-- TODO: Checks and triggers
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
    actor_id          uuid references actor,
    email             varchar(320) not null unique,
    created_timestamp timestamp not null
);

create table password_recovery_token
(
    token             varchar(73) primary key,
    actor_id          uuid references actor,
    created_timestamp timestamp not null
);

create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description text not null,
    logo_id     uuid references image -- TODO: Images question
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

-- TODO?: Subject request
create table subject
(
    id            uuid primary key,
    name          varchar(128) not null,
    description   varchar not null,
    brand_id      uuid not null references brand,
    is_shown      boolean not null,
    -- TODO: primary_image_id uuid not null references subject_image,
    reviews_count integer not null,  -- TODO: Trigger
    average_mark  float not null -- TODO: Trigger
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
    count integer,
    primary key (subject_id, mark)
);

create table subject_tag
(
    id   uuid primary key,
    name varchar(128) not null,
    subject_count integer not null           -- TODO: Trigger
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
    title          varchar(128) not null,
    reviewer_id    uuid not null references actor,
    subject_id     uuid not null references subject, -- TODO?: Subjects created by reviewer
    mark           integer not null,
    is_shown       boolean not null,
    upvotes_count   integer not null,           -- TODO: Trigger
    downvotes_count integer not null,           -- TODO: Trigger
    constraint review_mark_range_check check (mark >= 1 and mark <= 5)
);

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
    ordering  int,
    type      review_point_type not null,
    content   varchar(256) not null,
    primary key (review_id, ordering)
);

-- TODO: Images question
create table review_image
(
    id        uuid primary key,
    review_id uuid not null references review,
    ordering  integer not null
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
    created_timestamp timestamp not null,
    last_modified_timestamp timestamp not null,
    is_shown          boolean not null, -- TODO?: Moderation status
    upvotesCount      integer not null,     -- TODO: Trigger
    downvotesCount    integer not null default 0      -- TODO: Trigger
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
    last_modified_timestamp timestamp not null
);

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
    reason_id uuid references moderator_report_reason,
    report_id uuid references moderator_report,
    note varchar
);

create type email_task_type as enum ('SEND_EMAIL');

create table email_task
(
    id uuid primary key,
    type email_task_type not null,
    params varchar
);


create function update_subject_reviews_count() returns trigger as $update_subject_reviews_count$
begin
    update subject
    set reviews_count = reviews_count + 1
        where id = NEW.subject_id;
    return NEW;
end;
    $update_subject_reviews_count$ language plpgsql;

create trigger update_subject_reviews_count before insert on review
    for each row execute procedure update_subject_reviews_count();