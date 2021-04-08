create view subject_to_brand_preview_view as
select subject.id as subject_id,
       jsonb_build_object(
               'id', brand.id,
               'name', brand.name
           )      as brand_preview
from subject
         join brand on brand.id = subject.brand_id;

create view subject_to_marks_view as
select subject.id                               as subject_id,
       jsonb_agg(distinct jsonb_build_object(
               'value', subject_to_mark.mark,
               'count', subject_to_mark.count)) as marks
from subject
         join subject_to_mark on subject.id = subject_to_mark.subject_id
group by subject.id;

create view subject_to_tags_view as
select subject.id as subject_id,
       case count(subject_tag.id)
           when 0 then '[]'::jsonb
           else jsonb_agg(distinct jsonb_build_object(
                   'id', subject_tag.id,
                   'name', subject_tag.name
               ))
           end    as tags
from subject
         left join subject_to_tag stt on subject.id = stt.subject_id
         left join subject_tag on stt.tag_id = subject_tag.id
group by subject.id;

create view subject_full_view as
select subject.id                                  as id,
       subject.name                                as name,
       subject.description                         as description,
       subject.is_shown                            as is_shown,
       subject_to_marks_view.marks                 as marks,
       subject_to_tags_view.tags                   as tags,
       subject_to_brand_preview_view.brand_preview as brand_preview,
       image.location                              as image_location

from subject
         join subject_to_marks_view on subject.id = subject_to_marks_view.subject_id
         join subject_to_tags_view on subject.id = subject_to_tags_view.subject_id
         join subject_to_brand_preview_view on subject.id = subject_to_brand_preview_view.subject_id
         left join image on subject.primary_image_id = image.id;


create view subject_preview_view as
select subject.id                                  as id,
       subject.name                                as name,
       subject.is_shown                            as is_shown,
       subject_to_marks_view.marks                 as marks,
       subject_to_tags_view.tags                   as tags,
       subject_to_brand_preview_view.brand_preview as brand_preview,
       image.location                              as image_location

from subject
         join subject_to_marks_view on subject.id = subject_to_marks_view.subject_id
         join subject_to_tags_view on subject.id = subject_to_tags_view.subject_id
         join subject_to_brand_preview_view on subject.id = subject_to_brand_preview_view.subject_id
         left join image on subject.primary_image_id = image.id;