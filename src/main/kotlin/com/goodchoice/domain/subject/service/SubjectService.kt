package com.goodchoice.domain.subject.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.subject.persistence.SubjectRepository

interface SubjectService {
}

class SubjectServiceImpl(
    private val subjectRepo: SubjectRepository,
    private val authService: AuthService
) : SubjectService {

}