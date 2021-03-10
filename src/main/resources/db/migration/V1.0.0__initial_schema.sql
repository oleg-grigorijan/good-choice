-- TODO: Think about reviewer, employee, brand_presenter
-- TODO: Email verification (tokens)
-- TODO: Think about images
-- TODO: Moderator feedback
-- TODO?: Subject request
-- TODO?: Administrator report
-- TODO?: Email tasks

create table reviewer
(
    id                uuid primary key,
    first_name        varchar(64) not null,
    last_name         varchar(64) not null,
    email             varchar(320) not null unique,
    password          varchar(60) not null,
    created_timestamp timestamp not null,
    image_id          uuid
);

create type employee_role as enum ('HR', 'MODERATOR', 'ADMINISTRATOR');

create table employee
(
    id                uuid primary key,
    first_name        varchar(64) not null,
    last_name         varchar(64) not null,
    email             varchar(320) not null unique,
    password          varchar(60) not null,
    role              employee_role not null,
    created_timestamp timestamp not null
);

create table brand
(
    id          uuid primary key,
    name        varchar(64) not null,
    description text not null,
    image_id    uuid
);

create table brand_presenter
(
    id                uuid primary key,
    brand_id          uuid references brand,
    first_name        varchar(64),
    last_name         varchar(64),
    email             varchar(320) not null unique,
    password          varchar(60),
    created_timestamp timestamp not null
);

create table brand_registration_token
(
    token             uuid primary key,
    brand_id          uuid not null references brand,
    email             varchar(320) not null,
    created_timestamp timestamp not null
);

create table subject
(
    id          uuid primary key,
    name        varchar(128) not null,
    description text not null,
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
    reviewer_id uuid not null references reviewer,
    subject_id  uuid not null references subject, -- TODO?: Subjects created by reviewer
    mark        integer not null,
    is_accepted boolean not null,                 -- TODO?: Should it be placed here? Should it be state: ACCEPTED, DECLINED, MODERATING
    check (mark >= 0 and mark <= 5)
);

create table review_body
(
    id                uuid primary key,
    review_id         uuid not null references review,
    content           text not null,
    created_timestamp timestamp not null
);

create type review_point_type as enum ('ADVANTAGE', 'DISADVANTAGE');

create table review_point
(
    review_id uuid primary key references review,
    ordering  int primary key,
    type      review_point_type not null,
    content   varchar(256)
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
    review_id   uuid primary key references review,
    reviewer_id uuid primary key references reviewer,
    type        vote_type not null
);

create table review_comment
(
    id                uuid primary key,
    review_id         uuid not null references review,
    reviewer_id       uuid references reviewer,
    is_brand_response boolean not null,
    content           text not null,
    parent_comment_id uuid references review_comment,
    created_timestamp timestamp not null
);

create table review_comment_vote
(
    review_comment_id uuid primary key references review_comment,
    reviewer_id       uuid primary key references reviewer,
    type              vote_type not null
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
    description             text not null,
    issuer_reviewer_id      uuid references reviewer,
    issuer_type             report_issuer_type not null,
    status                  report_status not null,
    assignee_moderator_id   uuid references employee,
    created_timestamp       timestamp not null,
    last_modified_timestamp timestamp not null,
    check (num_nonnulls(review_id, review_comment_id) = 1)
);
