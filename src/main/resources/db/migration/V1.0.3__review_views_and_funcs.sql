create view review_to_review_points_view as
select review_id as review_id,
       case count(review_point) filter ( where review_point.type = 'ADVANTAGE' )
           when 0 then '[]'::jsonb
           else jsonb_agg(review_point.content) filter ( where review_point.type = 'ADVANTAGE' )
           end   as advantages,
       case count(review_point) filter ( where review_point.type = 'DISADVANTAGE' )
           when 0 then '[]'::jsonb
           else jsonb_agg(review_point.content) filter ( where review_point.type = 'DISADVANTAGE' )
           end   as disadvantages
from review_point
group by review_id;

create view review_to_review_bodies_view as
select review_id as review_id,
       case count(*)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'content', review_body.content,
                   'created_timestamp', review_body.created_timestamp
               ))
           end   as bodies
from review_body
group by review_id;

create view review_to_review_votes_count_view as
select id                                                  as review_id,
       count(*) filter ( where review_vote.type = 'UP' )   as upvotes_count,
       count(*) filter ( where review_vote.type = 'DOWN' ) as downvotes_count
from review
         left join review_vote on review.id = review_vote.review_id
group by review.id;

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
       review_to_review_votes_count_view.downvotes_count as downvotes_count
from review
         left join actor on review.reviewer_id = actor.id
         join review_to_review_points_view on review.id = review_to_review_points_view.review_id
         join review_to_review_bodies_view on review.id = review_to_review_bodies_view.review_id
         join review_to_review_votes_count_view on review.id = review_to_review_votes_count_view.review_id;

create function get_review_votes_by_actor(issuer_actor_id uuid)
    returns table
            (
                review_id       uuid,
                upvotes_count   bigint,
                downvotes_count bigint,
                own_vote        vote_type
            )
as
$$
begin
    return query
        select review_to_review_votes_count_view.review_id       as review_id,
               review_to_review_votes_count_view.upvotes_count   as upvotes_count,
               review_to_review_votes_count_view.downvotes_count as downvotes_count,
               review_vote.type                                  as own_vote
        from review_to_review_votes_count_view
                 left join review_vote on review_vote.review_id = review_to_review_votes_count_view.review_id and
                                          review_vote.reviewer_id = issuer_actor_id;
end;
$$ language plpgsql;

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
                own_vote          vote_type
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
               votes.own_vote                     as own_vote
        from review_full_view
                 join get_review_votes_by_actor(issuer_actor_id) votes on votes.review_id = review_full_view.id;
end;
$$ language plpgsql;