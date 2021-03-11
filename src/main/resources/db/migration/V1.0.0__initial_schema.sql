-- TODO: Think about images
-- TODO: Moderator feedback
-- TODO?: Subject request
-- TODO?: Administrator report
-- TODO?: Email tasks
-- TODO: now() time zone
-- TODO: Email verification: on update

create type actor_role as enum ('REVIEWER', 'HR', 'MODERATOR', 'ADMINISTRATOR', 'BRAND_PRESENTER');

create table actor
(
    id                  uuid primary key,
    first_name          varchar(64) not null,
    last_name           varchar(64) not null,
    email               varchar(320) not null unique,
    role                actor_role not null,
    password            varchar(60) not null,
    created_timestamp   timestamp not null default now(),
    image_id            uuid
);

create table email_verification_token
(
    token             varchar(73) primary key,
    actor_id          uuid references actor,
    email             varchar(320) not null unique,
    created_timestamp timestamp not null default now()
);

create table password_recovery_token
(
    token             varchar(73) primary key,
    actor_id          uuid references actor,
    created_timestamp timestamp not null default now()
);

create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description text not null,
    image_id    uuid
);

create table brand_presenter_details
(
    user_id  uuid primary key,
    brand_id uuid references brand
);

create table brand_registration_token
(
    token             varchar(73) primary key,
    brand_id          uuid not null references brand,
    email             varchar(320) not null,
    created_timestamp timestamp not null default now()
);

create table subject
(
    id          uuid primary key,
    name        varchar(128) not null,
    description varchar not null,
    brand_id    uuid not null references brand
    -- TODO?: images
);

create table subject_tag
(
    id   uuid primary key,
    name varchar(128)
);

create table subject_to_tag
(
    subject_id uuid primary key references subject,
    tag_id     uuid primary key references subject_tag
);

create table review
(
    id          uuid primary key,
    title       varchar(128) not null,
    reviewer_id uuid not null references actor,
    subject_id  uuid not null references subject, -- TODO?: Subjects created by reviewer
    mark        integer not null,
    is_accepted boolean not null,                 -- TODO?: Should it be placed here? Should it be state: ACCEPTED, DECLINED, MODERATING
    check (mark >= 0 and mark <= 5)
);

create table review_body
(
    id                uuid primary key,
    review_id         uuid not null references review,
    content           varchar not null,
    created_timestamp timestamp not null default now()
);

create type review_point_type as enum ('ADVANTAGE', 'DISADVANTAGE');

create table review_point
(
    review_id uuid references review,
    ordering  int,
    type      review_point_type not null,
    content   varchar(256),
    primary key (review_id, ordering)
);

create table review_image
(
    id        uuid primary key,
    review_id uuid not null references review,
    ordering  int not null
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
    reviewer_id       uuid references actor,
    is_brand_response boolean not null,
    content           varchar not null,
    parent_comment_id uuid references review_comment,
    created_timestamp timestamp not null default now()
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
    is_deleted boolean not null
);

create table moderator_report_to_reason
(
    report_id uuid primary key references moderator_report,
    reason_id uuid primary key references moderator_report_reason
);

create table moderator_report
(
    id                      uuid primary key,
    review_id               uuid references review,
    review_comment_id       uuid references review_comment,
    description             varchar not null,
    issuer_reviewer_id      uuid references actor,
    issuer_type             report_issuer_type not null,
    status                  report_status not null,
    assignee_moderator_id   uuid references actor,
    created_timestamp       timestamp not null default now(),
    last_modified_timestamp timestamp not null,
    check (num_nonnulls(review_id, review_comment_id) = 1)
);
