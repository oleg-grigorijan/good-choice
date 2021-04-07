package com.goodchoice.domain.subjectTag.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subjectTag.SubjectTagNotFoundException
import com.goodchoice.domain.subjectTag.model.SubjectTag
import com.goodchoice.domain.subjectTag.model.SubjectTagModificationRequest
import com.goodchoice.domain.subjectTag.persistence.SubjectTagRepository
import java.util.*

interface SubjectTagService {
    fun create(request: SubjectTagModificationRequest): Reference
    fun getById(id: UUID): SubjectTag
    fun update(id: UUID, request: SubjectTagModificationRequest)
    fun getAllByQuery(query: String, pageRequest: PageRequest): Page<SubjectTag>
}

class SubjectTagServiceImpl(private val subjectTagRepo: SubjectTagRepository, private val authService: AuthService) :
    SubjectTagService {

    override fun create(request: SubjectTagModificationRequest): Reference {
        return subjectTagRepo.create(request.name)
    }

    override fun getById(id: UUID): SubjectTag =
        subjectTagRepo.getByIdOrNull(id) ?: throw SubjectTagNotFoundException()

    override fun update(id: UUID, request: SubjectTagModificationRequest) {
        TODO("Not yet implemented")
    }

    override fun getAllByQuery(query: String, pageRequest: PageRequest): Page<SubjectTag> {
        TODO("Not yet implemented")
    }

}