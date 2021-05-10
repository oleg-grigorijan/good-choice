create view review_to_image_view as
select review.id as review_id,
       case count(*)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', review_image.image_id,
                   'location', image.location,
                   'ordering', review_image.ordering
               ))
           end   as images
from review
         join review_image on review.id = review_image.review_id
         join image on image.id = review_image.image_id
group by review_id;


create or replace view review_full_view as
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


drop function get_review_full_view_by_actor(issuer_actor_id uuid);

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
                 join get_review_votes_by_actor(issuer_actor_id) votes on votes.review_id = review_full_view.id;
end;
$$ language plpgsql;

alter table subject_image
    add column ordering integer not null default 0;

create view subject_to_image_view as
select subject.id as subject_id,
       case count(*)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', subject_image.image_id,
                   'location', image.location,
                   'ordering', subject_image.ordering
               ))
           end    as images
from subject
         join subject_image on subject.id = subject_image.subject_id
         join image on image.id = subject_image.image_id
group by subject_id;


create view subject_to_primary_image_view as
select subject.id as subject_id,
       case count(*)
           when 0 then '[]'::jsonb
           else jsonb_build_object(
                   'id', image.id,
                   'location', image.location
               )
           end    as image
from subject
         join image on image.id = subject.primary_image_id;


create or replace view subject_full_view as
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

create view subject_preview_view as
select subject.id                  as id,
       subject.name                as name,
       subject.is_shown            as is_shown,
       subject_to_marks_view.marks as marks,
       subject_to_tags_view.tags   as tags,
       brand.id                    as brand_id,
       brand.name                  as brand_name,
       image.location              as image_location
from subject
         join brand on brand.id = subject.brand_id
         join subject_to_marks_view on subject.id = subject_to_marks_view.subject_id
         join subject_to_tags_view on subject.id = subject_to_tags_view.subject_id
         left join image on subject.primary_image_id = image.id;