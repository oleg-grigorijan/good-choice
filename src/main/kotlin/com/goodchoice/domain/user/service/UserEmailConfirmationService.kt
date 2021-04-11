package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.user.UserExistsByEmailException
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserEmailConfirmationService {

    fun startConfirmationProcess(userId: UUID, email: Email)
}

class UserEmailConfirmationServiceImpl(private val authService: AuthService) : UserEmailConfirmationService {

    @Transactional
    override fun startConfirmationProcess(userId: UUID, email: Email) {
        forbid(authService.existsByEmail(email)) { UserExistsByEmailException(email) }

        // TODO(#10): Generate email confirmation token, send confirmation link to email
        authService.assignEmail(userId, email)
    }
}
