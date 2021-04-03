package com.goodchoice.domain.brand.service

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.auth.model.requireRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandModificationRequest
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.brand.model.BrandQueryRequest
import com.goodchoice.domain.brand.persistence.BrandRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface BrandService {
    fun create(request: BrandModificationRequest): UUID
    fun getById(id: UUID): Brand
    fun edit(id: UUID, request: BrandModificationRequest)
    fun getPreviewsByQuery(queryRequest: BrandQueryRequest): List<BrandPreview>
}

class BrandServiceImpl(
    private val brandRepo: BrandRepository,
    private val authService: AuthService
) : BrandService {

    @Transactional
    override fun create(request: BrandModificationRequest): UUID {
        return brandRepo.create(
            request.name,
            request.description
        )
    }

    @Transactional
    override fun getById(id: UUID): Brand {
        return brandRepo.getById(id)
    }

    @Transactional
    override fun edit(id: UUID, request: BrandModificationRequest) {
        brandRepo.update(
            id,
            request.name,
            request.description
        )
    }

    @Transactional
    override fun getPreviewsByQuery(queryRequest: BrandQueryRequest): List<BrandPreview> {
        authService.currentAuth.requireRole(UserRole.ADMINISTRATOR)
        return brandRepo.getPreviewsByQuery(queryRequest.query, queryRequest.limit, queryRequest.offset)
    }
}
