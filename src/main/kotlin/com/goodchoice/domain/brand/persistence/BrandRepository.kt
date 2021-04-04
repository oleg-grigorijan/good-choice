package com.goodchoice.domain.brand.persistence

import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.jooq.Tables.BRAND
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import org.jooq.DSLContext
import java.util.*


interface BrandRepository {
    fun create(name: String, description: String): Reference
    fun update(id: UUID, name: String, description: String)
    fun getAllPreviewsByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview>
    fun getByIdOrNull(id: UUID): Brand?
}

class BrandJooqRepository(private val db: DSLContext) : BrandRepository {
    override fun create(name: String, description: String): Reference {
        val id = UUID.randomUUID()
        db.insertInto(BRAND)
            .set(BRAND.ID, id)
            .set(BRAND.NAME, name)
            .set(BRAND.DESCRIPTION, description)
            .set(BRAND.IS_ACTIVE, true)
            .execute()
        return Reference(id)
    }

    override fun update(id: UUID, name: String, description: String) {
        db.update(BRAND)
            .set(BRAND.NAME, name)
            .set(BRAND.DESCRIPTION, description)
            .where(BRAND.ID.eq(id))
            .execute()
    }

    override fun getAllPreviewsByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview> {
        val limit = pageRequest.limit
        val offset = pageRequest.offset
        var hasNext = false
        val items = db.select(BRAND.ID, BRAND.NAME)
            .from(BRAND)
            .where(
                BRAND.IS_ACTIVE.eq(true)
                    .and(BRAND.NAME.like("%$query%"))
            )
            .limit(limit + 1)
            .offset(offset)
            .fetch()
            .map { BrandPreview(it[BRAND.ID], it[BRAND.NAME]) }
        if (items.size == limit + 1) {
            items.removeLast()
            hasNext = true
        }
        return Page(offset, items, hasNext)
    }

    override fun getByIdOrNull(id: UUID): Brand? =
        db.select(BRAND.ID, BRAND.NAME, BRAND.DESCRIPTION)
            .from(BRAND)
            .where(
                BRAND.ID.eq(id)
                    .and(BRAND.IS_ACTIVE.eq(true))
            )
            .fetchOne()
            ?.map { Brand(it[BRAND.ID], it[BRAND.NAME], it[BRAND.DESCRIPTION]) }
}