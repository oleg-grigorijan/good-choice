package com.goodchoice.domain.user.service

import com.goodchoice.domain.common.model.encode
import com.goodchoice.domain.user.model.ReviewerRegistrationRequest
import com.goodchoice.domain.user.persistence.ReviewerRepository
import com.goodchoice.domain.user.persistence.UserRepository
import com.goodchoice.infra.common.forbid
import com.goodchoice.infra.persistence.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface ReviewerService {

    fun register(request: ReviewerRegistrationRequest)
}

@Service
class ReviewerServiceImpl(
    private val userRepo: UserRepository,
    private val reviewerRepo: ReviewerRepository,
    private val passwordEncoder: PasswordEncoder
) : ReviewerService {

    @Transactional
    override fun register(request: ReviewerRegistrationRequest) {
        forbid(userRepo.existsByEmail(request.email)) { RuntimeException() }

        reviewerRepo.create(
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            passwordHash = passwordEncoder.encode(request.password)
        )
    }
}
