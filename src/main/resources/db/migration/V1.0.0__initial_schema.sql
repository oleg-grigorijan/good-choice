-- TODO: Think about reviewer, employee, brand_presenter
-- TODO: Email verification (tokens)
-- TODO: Authentication info (password_hash?)

create table reviewer
(
    id                uuid primary key,
    first_name        varchar(128) not null,
    last_name         varchar(128) not null,
    email             varchar(320) not null unique,
    created_date_time timestamp not null,
    image_id          uuid
    -- TODO?: country, language
);

create type employee_role as enum ('HR', 'MODERATOR', 'ADMINISTRATOR');

create table employee
(
    id                uuid primary key,
    first_name        varchar(128) not null,
    last_name         varchar(128) not null,
    email             varchar(320) not null unique,
    role              employee_role not null,
    created_date_time timestamp not null
);

create table brand
(
    id          uuid primary key,
    name        varchar(128) not null,
    description text not null,
    image_id    uuid
);

create table brand_presenter
(
    id                uuid primary key,
    brand_id          uuid references brand,
    email             varchar(320) not null unique,
    created_date_time timestamp not null
);

create table brand_registration_token
(
    id                uuid primary key,
    brand_id          uuid not null references brand,
    created_date_time timestamp not null,
    email             varchar(320) not null
);

create table subject
(
    id          uuid primary key,
    name        varchar(128) not null,
    description text not null,
    brand_id    uuid not null references brand
);
-- TODO?: images

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
    is_accepted bool not null -- TODO?: Should it be placed here? Should it be state: ACCEPTED, DECLINED, MODERATING
);

create table review_body
(
    id                uuid primary key,
    review_id         uuid not null references review,
    content           text not null,
    created_date_time timestamp not null
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
    is_brand_response bool not null,
    content           text not null,
    parent_comment_id uuid references review_comment,
    created_date_time timestamp not null
);

create table review_comment_vote
(
    review_comment_id uuid primary key references review_comment,
    reviewer_id       uuid primary key references reviewer,
    type              vote_type not null
);

create type moderator_report_issuer_type as enum ('GUEST', 'REVIEWER', 'BRAND_OWNER');

-- TODO?: One table for all reports
-- TODO: Moderator feedback
create table moderator_report
(
    id                 uuid primary key,
    review_id          uuid references review,
    review_comment_id  uuid references review_comment,
    description        text not null,
    issuer_reviewer_id uuid references reviewer,
    issuer_type        moderator_report_issuer_type not null
);

-- TODO: Administrator task (subject creation)
