create or replace view review_to_review_bodies_view as
select review_id as review_id,
       case count(*)
           when 0 then '[]'::jsonb
           else jsonb_agg(jsonb_build_object(
                   'id', review_body.id,
                   'content', review_body.content,
                   'createdTimestamp', review_body.created_timestamp
               ))
           end   as bodies
from review_body
group by review_id;