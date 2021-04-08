package com.goodchoice.domain.subject.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.common.jooq.Tables.*
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.Subject
import com.goodchoice.domain.subject.model.SubjectPreview
import com.goodchoice.domain.subject.model.SubjectSummary
import com.goodchoice.infra.persistence.read
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

interface SubjectRepository {
    fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference
    fun update(
        id: UUID,
        name: String,
        description: String,
        brand: Reference,
        addedTags: List<Reference>,
        removedTags: List<Reference>
    )

    fun getAllPreviewsByQuery(
        query: String?,
        brandId: UUID?,
        tagId: UUID?,
        pageRequest: PageRequest
    ): Page<SubjectPreview>

    fun getByIdOrNull(id: UUID): Subject?
}

class SubjectJooqRepository(
    private val db: DSLContext,
    private val clock: Clock,
    private val objectMapper: ObjectMapper
) : SubjectRepository {
    override fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference {
        val id = UUID.randomUUID()


        //todo: prevent from inserting into subject if inserting subject_to_tag throws
        db.insertInto(SUBJECT)
            .set(SUBJECT.ID, id)
            .set(SUBJECT.NAME, name)
            .set(SUBJECT.DESCRIPTION, description)
            .set(SUBJECT.BRAND_ID, brand.id)
            .set(SUBJECT.IS_SHOWN, true)
            .set(SUBJECT.CREATED_TIMESTAMP, LocalDateTime.now(clock))
            .execute()

        //todo: check if that's ok for no tags passed
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
        removedTags: List<Reference>
    ) {

        //todo: throw bad request if error while inserting happens

        db.update(SUBJECT)
            .set(SUBJECT.ID, id)
            .set(SUBJECT.NAME, name)
            .set(SUBJECT.DESCRIPTION, description)
            .set(SUBJECT.BRAND_ID, brand.id)
            .where(SUBJECT.ID.eq(id))
            .execute()

        //todo: check if that's ok for no tags passed
        db.insertInto(SUBJECT_TO_TAG, SUBJECT_TO_TAG.SUBJECT_ID, SUBJECT_TO_TAG.TAG_ID)
            .apply { addedTags.forEach { values(id, it.id) } }
            .execute()

        //todo: check if that's ok for no tags passed

        db.delete(SUBJECT_TO_TAG)
            .where(
                SUBJECT_TO_TAG.SUBJECT_ID.eq(id).and(
                    removedTags.map { SUBJECT_TO_TAG.TAG_ID.eq(it.id) }
                        .fold(DSL.falseCondition() as Condition, { ac, e -> ac.or(e) })
                )
            )
            .execute()

    }

    override fun getAllPreviewsByQuery(
        query: String?,
        brandId: UUID?,
        tagId: UUID?,
        pageRequest: PageRequest
    ): Page<SubjectPreview> {
        val limit = pageRequest.limit
        val offset = pageRequest.offset

        val items = db.select()
            .from(SUBJECT_PREVIEW_VIEW)
            .where(
                SUBJECT_PREVIEW_VIEW.IS_SHOWN.eq(true)
                    .and(SUBJECT_PREVIEW_VIEW.NAME.likeIgnoreCase("%$query%"))
            )
            .limit(limit + 1)
            .offset(offset)
            .fetch()
            .map {
                SubjectPreview(
                    id = it[SUBJECT_PREVIEW_VIEW.ID],
                    name = it[SUBJECT_PREVIEW_VIEW.NAME],
                    brand = it[SUBJECT_PREVIEW_VIEW.BRAND_PREVIEW].read(objectMapper),
                    summary = SubjectSummary(it[SUBJECT_PREVIEW_VIEW.MARKS].read(objectMapper)),
                    subjectTags = it[SUBJECT_PREVIEW_VIEW.TAGS].read(objectMapper)
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
                    .and(SUBJECT_FULL_VIEW.IS_SHOWN.eq(true))
            )
            .fetchOne()
            ?.map {
                Subject(
                    id = it[SUBJECT_FULL_VIEW.ID],
                    name = it[SUBJECT_FULL_VIEW.NAME],
                    brand = it[SUBJECT_FULL_VIEW.BRAND_PREVIEW].read(objectMapper),
                    summary = SubjectSummary(it[SUBJECT_FULL_VIEW.MARKS].read(objectMapper)),
                    description = it[SUBJECT.DESCRIPTION],
                    subjectTags = it[SUBJECT_FULL_VIEW.TAGS].read(objectMapper)
                )
            }
    }
}