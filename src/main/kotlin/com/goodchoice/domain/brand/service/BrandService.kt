package com.goodchoice.domain.brand.service

import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandModificationRequest
import com.goodchoice.domain.brand.persistence.BrandRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface BrandService {
    fun create(request: BrandModificationRequest): UUID
    fun getById(id: UUID): Brand
    fun edit(id: UUID, request: BrandModificationRequest)
}

class BrandServiceImpl(
    private val brandRepo: BrandRepository
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
}
