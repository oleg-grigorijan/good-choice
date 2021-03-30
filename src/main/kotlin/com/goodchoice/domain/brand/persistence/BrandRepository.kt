package com.goodchoice.domain.brand.persistence

import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.common.jooq.Tables
import org.jooq.DSLContext
import java.util.*


interface BrandRepository {
    fun create(name: String, description: String): UUID
    fun update(id: UUID, name: String, description: String)
    fun getAllPreviews(): List<BrandPreview>
    fun getById(id: UUID): Brand
}

class BrandJooqRepository(private val db: DSLContext) : BrandRepository {
    override fun create(name: String, description: String): UUID {
        val id = UUID.randomUUID()
        db.insertInto(Tables.BRAND)
            .set(Tables.BRAND.ID, id)
            .set(Tables.BRAND.NAME, name)
            .set(Tables.BRAND.DESCRIPTION, description)
            .set(Tables.BRAND.IS_ACTIVE, true)
            .execute()
        return id
    }

    override fun update(id: UUID, name: String, description: String) {
        TODO("Not yet implemented")
    }

    override fun getAllPreviews(): List<BrandPreview> {
        return db.select(Tables.BRAND.ID, Tables.BRAND.NAME)
            .from(Tables.BRAND)
            .where(Tables.BRAND.IS_ACTIVE.eq(true))
            .fetch()
            .map { BrandPreview(it[Tables.BRAND.ID], it[Tables.BRAND.NAME]) }
    }

    override fun getById(id: UUID): Brand {
        return db.select(Tables.BRAND.ID, Tables.BRAND.NAME, Tables.BRAND.DESCRIPTION)
            .from(Tables.BRAND)
            .where(Tables.BRAND.ID.eq(id))
            .and(Tables.BRAND.IS_ACTIVE.eq(true))
            .fetch()
            .map { Brand(it[Tables.BRAND.ID], it[Tables.BRAND.NAME], it[Tables.BRAND.DESCRIPTION]) }[0]
    }
}