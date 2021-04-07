create view subject_full_view as
select subject.id                               as id,
       subject.name                             as name,
       subject.description                      as description,
       subject.is_shown                         as is_shown,
       jsonb_agg(distinct jsonb_build_object(
               'value', subject_to_mark.mark,
               'count', subject_to_mark.count)) as marks,
       case count(subject_tag.id)
           when 0 then '[]'::jsonb
           else jsonb_agg(distinct jsonb_build_object(
                   'id', subject_tag.id,
                   'name', subject_tag.name
               ))
           end                                  as tags,
       brand.id                                 as brand_id,
       brand.name                               as brand_name,
       image.location                           as image_location

from subject
         join subject_to_mark on subject.id = subject_to_mark.subject_id
         left join subject_to_tag stt on subject.id = stt.subject_id
         left join subject_tag on stt.tag_id = subject_tag.id
         join brand on subject.brand_id = brand.id
         left join image on subject.primary_image_id = image.id
group by subject.id, brand.id, image.id;
