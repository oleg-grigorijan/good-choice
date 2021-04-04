package com.goodchoice.domain.subject.persistence

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.Subject
import org.jooq.DSLContext
import java.util.*

interface SubjectRepository {
    fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference
    fun update(id: UUID, name: String, description: String, tags: List<Reference>, brand: Reference)

    //    fun getAllByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview>
    fun getByIdOrNull(id: UUID): Subject?
}

class SubjectJooqRepository(
    private val db: DSLContext
) : SubjectRepository {
    override fun create(name: String, description: String, tags: List<Reference>, brand: Reference): Reference {
        val id = UUID.randomUUID()

        //todo: insertInto SUBJECT_TO_TAG
//        db.insertInto(SUBJECT_TO_TAG, SUBJECT_TO_TAG.SUBJECT_ID, SUBJECT_TO_TAG.TAG_ID)
//            .values(tags.map{ db.newRecord(SUBJECT_TO_TAG.)})

//        db.insertInto(SUBJECT)
//            .set(SUBJECT.ID, id)
//            .set(SUBJECT.NAME, name)
//            .set(SUBJECT.DESCRIPTION, description)
//            .set(SUBJECT.BRAND_ID, brand.id)
//            .set(SUBJECT.IS_SHOWN, true)
//            .execute()

        return Reference(id)
    }

    override fun update(id: UUID, name: String, description: String, tags: List<Reference>, brand: Reference) {
        TODO("Not yet implemented")
    }

    override fun getByIdOrNull(id: UUID): Subject? {
        TODO("Not yet implemented")
    }
}