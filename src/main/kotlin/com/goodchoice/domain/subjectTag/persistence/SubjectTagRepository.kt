package com.goodchoice.domain.subjectTag.persistence

import com.goodchoice.domain.common.jooq.Tables.BRAND
import com.goodchoice.domain.common.jooq.Tables.SUBJECT_TAG
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subjectTag.model.SubjectTag
import org.jooq.DSLContext
import java.util.*

interface SubjectTagRepository {
    fun create(name: String): Reference
    fun update(id: UUID, name: String)
    fun getAllByQuery(query: String, pageRequest: PageRequest): Page<SubjectTag>
    fun getByIdOrNull(id: UUID): SubjectTag?
}

class SubjectTagJooqRepository(private val db: DSLContext) : SubjectTagRepository {
    override fun create(name: String): Reference {
        val id = UUID.randomUUID()
        db.insertInto(SUBJECT_TAG)
            .set(SUBJECT_TAG.ID, id)
            .set(SUBJECT_TAG.NAME, name)
            .execute()
        return Reference(id)
    }

    override fun update(id: UUID, name: String) {
        db.update(SUBJECT_TAG)
            .set(SUBJECT_TAG.NAME, name)
            .where(SUBJECT_TAG.ID.eq(id))
            .execute()
    }

    override fun getAllByQuery(query: String, pageRequest: PageRequest): Page<SubjectTag> {
        val limit = pageRequest.limit
        val offset = pageRequest.offset

        val items = db.select()
            .from(SUBJECT_TAG)
            .where(
                SUBJECT_TAG.NAME.likeIgnoreCase("%$query%")
            )
            .limit(limit + 1)
            .offset(offset)
            .fetch()
            .map { SubjectTag(it[SUBJECT_TAG.ID], it[SUBJECT_TAG.NAME], it[SUBJECT_TAG.SUBJECTS_COUNT]) }

        var hasNext = false
        if (items.size == limit + 1) {
            items.removeLast()
            hasNext = true
        }
        return Page(offset, items, hasNext)
    }

    override fun getByIdOrNull(id: UUID): SubjectTag? =
        db.select()
            .from(SUBJECT_TAG)
            .where(
                SUBJECT_TAG.ID.eq(id)
            )
            .fetchOne()
            ?.map { SubjectTag(it[BRAND.ID], it[BRAND.NAME], it[SUBJECT_TAG.SUBJECTS_COUNT]) }
}