package com.goodchoice.domain.subject.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.SubjectNotFoundException
import com.goodchoice.domain.subject.model.Subject
import com.goodchoice.domain.subject.model.SubjectModificationRequest
import com.goodchoice.domain.subject.persistence.SubjectRepository
import java.util.*

interface SubjectService {
    fun create(subjectModificationRequest: SubjectModificationRequest): Reference
    fun getById(id: UUID): Subject
}

class SubjectServiceImpl(
    private val subjectRepo: SubjectRepository,
    private val authService: AuthService
) : SubjectService {
    override fun create(subjectModificationRequest: SubjectModificationRequest): Reference {
        return subjectRepo.create(
            name = subjectModificationRequest.name,
            description = subjectModificationRequest.description,
            tags = subjectModificationRequest.tags,
            brand = subjectModificationRequest.brand
        )
    }

    override fun getById(id: UUID): Subject =
        subjectRepo.getByIdOrNull(id) ?: throw SubjectNotFoundException()

}