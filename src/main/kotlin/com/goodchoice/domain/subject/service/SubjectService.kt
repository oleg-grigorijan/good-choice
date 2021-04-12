package com.goodchoice.domain.subject.service

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.auth.model.requireRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.SubjectNotFoundException
import com.goodchoice.domain.subject.model.*
import com.goodchoice.domain.subject.persistence.SubjectRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface SubjectService {
    fun create(request: SubjectCreationRequest): Reference
    fun getById(id: UUID): Subject
    fun update(id: UUID, request: SubjectModificationRequest)
    fun getAllPreviewsByQuery(subjectQuery: SubjectQuery, pageRequest: PageRequest): Page<SubjectPreview>
}

class SubjectServiceImpl(
    private val subjectRepo: SubjectRepository,
    private val authService: AuthService
) : SubjectService {

    @Transactional
    override fun create(request: SubjectCreationRequest): Reference {
        authService.currentAuth.requireRole(UserRole.ADMINISTRATOR)
        return subjectRepo.create(
            name = request.name,
            description = request.description,
            tags = request.addedTags,
            brand = request.brand
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): Subject =
        subjectRepo.getByIdOrNull(id) ?: throw SubjectNotFoundException()

    @Transactional
    override fun update(id: UUID, request: SubjectModificationRequest) {
        authService.currentAuth.requireRole(UserRole.ADMINISTRATOR)
        subjectRepo.update(
            id = id,
            name = request.name,
            description = request.description,
            brand = request.brand,
            addedTags = request.addedTags,
            removedTags = request.removedTags
        )
    }

    @Transactional(readOnly = true)
    override fun getAllPreviewsByQuery(subjectQuery: SubjectQuery, pageRequest: PageRequest): Page<SubjectPreview> =
        subjectRepo.getAllPreviewsByQuery(
            query = subjectQuery.query,
            brandId = subjectQuery.brandId,
            pageRequest = pageRequest
        )


}