package com.goodchoice.domain.brand.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandModificationRequest
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.brand.persistence.BrandRepository
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface BrandService {
    fun create(request: BrandModificationRequest): UUID
    fun getById(id: UUID): Brand
    fun update(id: UUID, request: BrandModificationRequest)
    fun getAllPreviewsByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview>
}

class BrandServiceImpl(
    private val brandRepo: BrandRepository,
    private val authService: AuthService
) : BrandService {

    @Transactional
    override fun create(request: BrandModificationRequest): UUID {
        return brandRepo.create(
            name = request.name,
            description = request.description
        )
    }

    @Transactional
    override fun getById(id: UUID): Brand {
        return brandRepo.getByIdOrNull(id) ?: throw RuntimeException()
    }

    @Transactional
    override fun update(id: UUID, request: BrandModificationRequest) {
        brandRepo.update(
            id = id,
            name = request.name,
            description = request.description
        )
    }

    @Transactional
    override fun getAllPreviewsByQuery(query: String, pageRequest: PageRequest): Page<BrandPreview> {
        return brandRepo.getAllPreviewsByQuery(
            query = query,
            limit = pageRequest.limit,
            offset = pageRequest.offset
        )
    }
}
