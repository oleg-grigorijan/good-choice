package com.goodchoice.domain.image.persistence

import com.goodchoice.domain.common.jooq.Tables.IMAGE
import com.goodchoice.domain.common.model.Reference
import org.jooq.DSLContext
import java.net.URL
import java.util.*

interface ImageRepository {
    fun add(source: URL): Reference
}

class ImageRepositoryImpl(private val db: DSLContext) : ImageRepository {
    override fun add(source: URL): Reference {
        val id = UUID.randomUUID()
        db.insertInto(IMAGE)
            .set(IMAGE.ID, id)
            .set(IMAGE.LOCATION, source.toString())
            .execute()
        return Reference(id)
    }

}