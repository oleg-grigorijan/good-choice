package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.ReviewerRegistrationRequest
import com.goodchoice.domain.user.persistence.ReviewerRepository
import org.springframework.transaction.annotation.Transactional

interface ReviewerSelfService {

    fun register(request: ReviewerRegistrationRequest)
}

class ReviewerSelfServiceImpl(
    private val reviewerRepo: ReviewerRepository,
    private val authService: AuthService,
    private val emailConfirmationService: UserEmailConfirmationService
) : ReviewerSelfService {

    @Transactional
    override fun register(request: ReviewerRegistrationRequest) {
        val reviewerId = reviewerRepo.create(
            firstName = request.firstName,
            lastName = request.lastName,
            passwordHash = authService.generatePasswordHash(request.password)
        )
        emailConfirmationService.startConfirmationProcess(reviewerId, request.email)
    }
}
