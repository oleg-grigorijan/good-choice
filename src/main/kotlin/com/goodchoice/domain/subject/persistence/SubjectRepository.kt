package com.goodchoice.domain.subject.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.jooq.Tables.*
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.Subject
import com.goodchoice.domain.subject.model.SubjectPreview
import com.goodchoice.domain.subject.model.SubjectSummary
import com.goodchoice.infra.common.now
import com.goodchoice.infra.persistence.read
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.Clock
import java.util.*

interface SubjectRepository {
    fun create(
        name: String,
        description: String,
        tags: List<Reference>,
        brand: Reference,
        images: List<Reference>,
        primaryImage: Reference?
    ): Reference

    fun update(
        id: UUID,
        name: String,
        description: String,
        brand: Reference,
        addedTags: List<Reference>,
        removedTags: List<Reference>,
        images: List<Reference>,
        primaryImage: Reference?
    )

    fun getAllPreviewsByQuery(
        query: String?,
        brandId: UUID?,
        pageRequest: PageRequest
    ): Page<SubjectPreview>

    fun getByIdOrNull(id: UUID): Subject?
}

class SubjectJooqRepository(
    private val db: DSLContext,
    private val clock: Clock,
    private val objectMapper: ObjectMapper
) : SubjectRepository {
    override fun create(
        name: String,
        description: String,
        tags: List<Reference>,
        brand: Reference,
        images: List<Reference>,
        primaryImage: Reference?
    ): Reference {
        val id = UUID.randomUUID()

        var ordering = 0
        db.insertInto(SUBJECT_IMAGE, SUBJECT_IMAGE.SUBJECT_ID, SUBJECT_IMAGE.IMAGE_ID, SUBJECT_IMAGE.ORDERING)
            .apply { images.forEach { values(id, it.id, ordering++) } }
            .execute()

        db.insertInto(SUBJECT)
            .set(SUBJECT.ID, id)
            .set(SUBJECT.NAME, name)
            .set(SUBJECT.DESCRIPTION, description)
            .set(SUBJECT.BRAND_ID, brand.id)
            .set(SUBJECT.IS_SHOWN, true)
            .set(SUBJECT.CREATED_TIMESTAMP, clock.now())
            .set(SUBJECT.PRIMARY_IMAGE_ID, primaryImage?.id)
            .execute()

        db.insertInto(SUBJECT_TO_TAG, SUBJECT_TO_TAG.SUBJECT_ID, SUBJECT_TO_TAG.TAG_ID)
            .apply { tags.forEach { values(id, it.id) } }
            .execute()

        return Reference(id)
    }

    override fun update(
        id: UUID,
        name: String,
        description: String,
        brand: Reference,
        addedTags: List<Reference>,
        removedTags: List<Reference>,
        images: List<Reference>,
        primaryImage: Reference?
    ) {

        //need to insert images first in order to be able to manage primaryImage setting correctly
        var ordering = 0
        db.insertInto(SUBJECT_IMAGE, SUBJECT_IMAGE.SUBJECT_ID, SUBJECT_IMAGE.IMAGE_ID, SUBJECT_IMAGE.ORDERING)
            .apply { images.forEach { values(id, it.id, ordering++) } }
            .onConflict(SUBJECT_IMAGE.SUBJECT_ID, SUBJECT_IMAGE.IMAGE_ID)
            .doUpdate()
            .set(SUBJECT_IMAGE.ORDERING, DSL.field("EXCLUDED.ordering", SUBJECT_IMAGE.ORDERING.dataType))
            .execute()

        db.update(SUBJECT)
            .set(SUBJECT.NAME, name)
            .set(SUBJECT.DESCRIPTION, description)
            .set(SUBJECT.BRAND_ID, brand.id)
            .set(SUBJECT.PRIMARY_IMAGE_ID, primaryImage?.id)
            .where(SUBJECT.ID.eq(id))
            .execute()

        db.delete(SUBJECT_IMAGE)
            .where(SUBJECT_IMAGE.SUBJECT_ID.eq(id).and(SUBJECT_IMAGE.IMAGE_ID.notIn(images.map { it.id })))
            .execute()

        db.insertInto(SUBJECT_TO_TAG, SUBJECT_TO_TAG.SUBJECT_ID, SUBJECT_TO_TAG.TAG_ID)
            .apply { addedTags.forEach { values(id, it.id) } }
            .execute()

        db.delete(SUBJECT_TO_TAG)
            .where(
                SUBJECT_TO_TAG.SUBJECT_ID.eq(id)
                    .and(SUBJECT_TO_TAG.TAG_ID.`in`(removedTags.map { it.id }))
            )
            .execute()
    }

    override fun getAllPreviewsByQuery(
        query: String?,
        brandId: UUID?,
        pageRequest: PageRequest
    ): Page<SubjectPreview> {
        val limit = pageRequest.limit
        val offset = pageRequest.offset

        val items = db.select()
            .from(SUBJECT_PREVIEW_VIEW)
            .where(
                SUBJECT_PREVIEW_VIEW.IS_SHOWN.eq(true)
                    .let { condition ->
                        if (brandId != null) {
                            condition.and(SUBJECT_PREVIEW_VIEW.BRAND_ID.eq(brandId))
                        } else {
                            condition
                        }
                    }
                    .let { condition ->
                        if (query != null) {
                            condition.and(SUBJECT_PREVIEW_VIEW.NAME.containsIgnoreCase(query))
                        } else {
                            condition
                        }
                    }
            )
            .limit(limit + 1)
            .offset(offset)
            .fetch()
            .map {
                SubjectPreview(
                    id = it[SUBJECT_PREVIEW_VIEW.ID],
                    name = it[SUBJECT_PREVIEW_VIEW.NAME],
                    brand = BrandPreview(it[SUBJECT_PREVIEW_VIEW.BRAND_ID], it[SUBJECT_PREVIEW_VIEW.BRAND_NAME]),
                    summary = SubjectSummary(objectMapper.read(it[SUBJECT_PREVIEW_VIEW.MARKS])),
                    subjectTags = objectMapper.read(it[SUBJECT_PREVIEW_VIEW.TAGS]),
                    primaryImage = it[SUBJECT_PREVIEW_VIEW.PRIMARY_IMAGE]?.let { objectMapper.read(it) }
                )
            }
        var hasNext = false
        if (items.size == limit + 1) {
            items.removeLast()
            hasNext = true
        }
        return Page(offset, items, hasNext)
    }


    override fun getByIdOrNull(id: UUID): Subject? {
        return db.select()
            .from(SUBJECT_FULL_VIEW)
            .where(
                SUBJECT_FULL_VIEW.ID.eq(id)
                    .and(SUBJECT_FULL_VIEW.IS_SHOWN)
            )
            .fetchOne()
            ?.map {
                Subject(
                    id = it[SUBJECT_FULL_VIEW.ID],
                    name = it[SUBJECT_FULL_VIEW.NAME],
                    brand = BrandPreview(it[SUBJECT_FULL_VIEW.BRAND_ID], it[SUBJECT_FULL_VIEW.BRAND_NAME]),
                    summary = SubjectSummary(objectMapper.read(it[SUBJECT_FULL_VIEW.MARKS])),
                    description = it[SUBJECT.DESCRIPTION],
                    subjectTags = objectMapper.read(it[SUBJECT_FULL_VIEW.TAGS]),
                    images = objectMapper.read(it[SUBJECT_FULL_VIEW.IMAGES]),
                    primaryImage = it[SUBJECT_FULL_VIEW.PRIMARY_IMAGE]?.let { objectMapper.read(it) }
                )
            }
    }
}
