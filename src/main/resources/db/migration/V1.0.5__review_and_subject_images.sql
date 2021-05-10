create or replace view review_to_image_view as
select review.id as review_id,
       case count(review_image.review_id)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', review_image.image_id,
                   'location', image.location,
                   'ordering', review_image.ordering
               ))
           end   as images
from review
         left join review_image on review.id = review_image.review_id
         left join image on image.id = review_image.image_id
group by review.id;

drop view if exists review_full_view;
create view review_full_view as
select review.id                                         as id,
       review.title                                      as title,
       review.is_shown                                   as is_shown,
       review.subject_id                                 as subject_id,
       review.mark                                       as mark,
       actor.id                                          as author_id,
       actor.first_name                                  as author_first_name,
       actor.last_name                                   as author_last_name,
       review_to_review_points_view.advantages           as advantages,
       review_to_review_points_view.disadvantages        as disadvantages,
       review_to_review_bodies_view.bodies               as bodies,
       review_to_review_votes_count_view.upvotes_count   as upvotes_count,
       review_to_review_votes_count_view.downvotes_count as downvotes_count,
       review_to_image_view.images                       as images
from review
         left join actor on review.reviewer_id = actor.id
         join review_to_review_points_view on review.id = review_to_review_points_view.review_id
         join review_to_review_bodies_view on review.id = review_to_review_bodies_view.review_id
         join review_to_review_votes_count_view on review.id = review_to_review_votes_count_view.review_id
         join review_to_image_view on review.id = review_to_image_view.review_id;


drop function if exists get_review_full_view_by_actor(issuer_actor_id uuid);
create function get_review_full_view_by_actor(issuer_actor_id uuid)
    returns table
            (
                id                uuid,
                title             varchar(128),
                is_shown          boolean,
                subject_id        uuid,
                mark              integer,
                author_id         uuid,
                author_first_name varchar(64),
                author_last_name  varchar(64),
                advantages        jsonb,
                disadvantages     jsonb,
                bodies            jsonb,
                upvotes_count     bigint,
                downvotes_count   bigint,
                own_vote          vote_type,
                images            jsonb
            )
as
$$
begin
    return query
        select review_full_view.id                as id,
               review_full_view.title             as title,
               review_full_view.is_shown          as is_shown,
               review_full_view.subject_id        as subject_id,
               review_full_view.mark              as mark,
               review_full_view.author_id         as author_id,
               review_full_view.author_first_name as author_first_name,
               review_full_view.author_last_name  as author_last_name,
               review_full_view.advantages        as advantages,
               review_full_view.disadvantages     as disadvantages,
               review_full_view.bodies            as bodies,
               votes.upvotes_count                as upvotes_count,
               votes.downvotes_count              as downvotes_count,
               votes.own_vote                     as own_vote,
               review_full_view.images            as images
        from review_full_view
                 left join get_review_votes_by_actor(issuer_actor_id) votes on votes.review_id = review_full_view.id;
end;
$$ language plpgsql;

alter table subject_image
    add column ordering integer not null default 0;

create view subject_to_primary_image_view as
select subject.id as subject_id,
       case count(subject.primary_image_id)
           when 0 then null
           else jsonb_build_object(
                   'id', image.id,
                   'location', image.location
               )
           end    as image
from subject
         left join image on image.id = subject.primary_image_id
group by subject.id, image.id;


create view subject_to_image_view as
select subject.id as subject_id,
       case count(subject_image.subject_id)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', subject_image.image_id,
                   'location', image.location,
                   'ordering', subject_image.ordering
               ))
           end    as images
from subject
         left join subject_image on subject.id = subject_image.subject_id
         left join image on image.id = subject_image.image_id
group by subject.id;

drop view if exists subject_full_view;
create view subject_full_view as
select subject.id                          as id,
       subject.name                        as name,
       subject.description                 as description,
       subject.is_shown                    as is_shown,
       subject_to_marks_view.marks         as marks,
       subject_to_tags_view.tags           as tags,
       brand.id                            as brand_id,
       brand.name                          as brand_name,
       subject_to_primary_image_view.image as primary_image,
       subject_to_image_view.images        as images
from subject
         join brand on brand.id = subject.brand_id
         join subject_to_marks_view on subject.id = subject_to_marks_view.subject_id
         join subject_to_tags_view on subject.id = subject_to_tags_view.subject_id
         join subject_to_primary_image_view on subject.id = subject_to_primary_image_view.subject_id
         join subject_to_image_view on subject.id = subject_to_image_view.subject_id;

drop view if exists subject_preview_view;
create view subject_preview_view as
select subject.id                          as id,
       subject.name                        as name,
       subject.is_shown                    as is_shown,
       subject_to_marks_view.marks         as marks,
       subject_to_tags_view.tags           as tags,
       brand.id                            as brand_id,
       brand.name                          as brand_name,
       subject_to_primary_image_view.image as primary_image
from subject
         join brand on brand.id = subject.brand_id
         join subject_to_marks_view on subject.id = subject_to_marks_view.subject_id
         join subject_to_tags_view on subject.id = subject_to_tags_view.subject_id
         join subject_to_primary_image_view on subject.id = subject_to_primary_image_view.subject_id;

alter table subject_image
    add unique (subject_id, ordering);

alter table review_image
    add unique (review_id, ordering);

create or replace view review_to_review_points_view as
select review.id as review_id,
       case count(review_point.review_id) filter ( where review_point.type = 'ADVANTAGE' )
           when 0 then '[]'::jsonb
           else jsonb_agg(review_point.content) filter ( where review_point.type = 'ADVANTAGE' )
           end   as advantages,
       case count(review_point.review_id) filter ( where review_point.type = 'DISADVANTAGE' )
           when 0 then '[]'::jsonb
           else jsonb_agg(review_point.content) filter ( where review_point.type = 'DISADVANTAGE' )
           end   as disadvantages
from review
         left join review_point on review.id = review_point.review_id
group by review.id;

create or replace view review_to_review_bodies_view as
select review.id as review_id,
       case count(review_body.review_id)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', review_body.id,
                   'content', review_body.content,
                   'createdTimestamp', review_body.created_timestamp
               ))
           end   as bodies
from review
         left join review_body on review.id = review_body.review_id
group by review.id;