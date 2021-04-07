package com.goodchoice.domain.subject.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.jooq.Tables.SUBJECT
import com.goodchoice.domain.common.jooq.Tables.SUBJECT_FULL_VIEW
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.MarkDetails
import com.goodchoice.domain.subject.model.Subject
import com.goodchoice.domain.subject.model.SubjectSummary
import com.goodchoice.infra.persistence.readList
import org.jooq.DSLContext
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

interface SubjectRepository {
    fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference
    fun update(id: UUID, name: String, description: String, tags: List<Reference>, brand: Reference)

    //    fun getAllByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview>
    fun getByIdOrNull(id: UUID): Subject?
}

class SubjectJooqRepository(
    private val db: DSLContext,
    private val clock: Clock,
    private val objectMapper: ObjectMapper
) : SubjectRepository {
    override fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference {
        val id = UUID.randomUUID()

        //todo: insertInto SUBJECT_TO_TAG
//        db.insertInto(SUBJECT_TO_TAG, SUBJECT_TO_TAG.SUBJECT_ID, SUBJECT_TO_TAG.TAG_ID)
//            .values(tags.map{ db.newRecord(SUBJECT_TO_TAG.)})

        db.insertInto(SUBJECT)
            .set(SUBJECT.ID, id)
            .set(SUBJECT.NAME, name)
            .set(SUBJECT.DESCRIPTION, description)
            .set(SUBJECT.BRAND_ID, brand.id)
            .set(SUBJECT.IS_SHOWN, true)
            .set(SUBJECT.CREATED_TIMESTAMP, LocalDateTime.now(clock))
            .execute()

        return Reference(id)
    }

    override fun update(id: UUID, name: String, description: String, tags: List<Reference>, brand: Reference) {
        TODO("Not yet implemented")
    }

    override fun getByIdOrNull(id: UUID): Subject? {
        // todo: map it[SUBJECT_FULL_VIEW.MARKS] to List<MarkDetails>
        return db.select()
            .from(SUBJECT_FULL_VIEW)
            .where(
                SUBJECT_FULL_VIEW.ID.eq(id)
                    .and(SUBJECT_FULL_VIEW.IS_SHOWN.eq(true))
            )
            .fetchOne()
            ?.map {
                val tmp = it[SUBJECT_FULL_VIEW.MARKS].readList<MarkDetails>(objectMapper)
                Subject(
                    id = it[SUBJECT_FULL_VIEW.ID],
                    name = it[SUBJECT_FULL_VIEW.NAME],
                    brand = BrandPreview(it[SUBJECT_FULL_VIEW.BRAND_ID], it[SUBJECT_FULL_VIEW.BRAND_NAME]),
                    summary = SubjectSummary(it[SUBJECT_FULL_VIEW.MARKS].readList(objectMapper)),
                    description = it[SUBJECT.DESCRIPTION],
                )
            }
    }
}