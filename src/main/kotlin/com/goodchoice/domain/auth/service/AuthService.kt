package com.goodchoice.domain.auth.service

import com.goodchoice.domain.auth.AuthenticationRequiredException
import com.goodchoice.domain.auth.InvalidCredentialsException
import com.goodchoice.domain.auth.model.Auth
import com.goodchoice.domain.auth.model.AuthWithCredentials
import com.goodchoice.domain.auth.persistence.AuthRepository
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.NewPassword
import com.goodchoice.domain.common.model.encode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface AuthContext {

    val currentAuthOrNull: Auth?
    val currentAuth: Auth
        get() = currentAuthOrNull ?: throw AuthenticationRequiredException()
}

interface AuthService : AuthContext {

    fun existsByEmail(email: Email): Boolean
    fun getCredentialsByEmailOrNull(email: Email): AuthWithCredentials?
    fun getByCredentialsOrNull(email: Email, password: String): Auth?
    fun getByCredentials(email: Email, password: String): Auth
    fun assignEmail(userId: UUID, email: Email)
    fun generatePasswordHash(password: NewPassword): String
}

class AuthServiceImpl(
    private val authRepo: AuthRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthService {

    override val currentAuthOrNull: Auth?
        get() = SecurityContextHolder.getContext().authentication?.principal as? Auth

    @Transactional(readOnly = true)
    override fun existsByEmail(email: Email): Boolean =
        authRepo.existsByEmail(email)

    @Transactional(readOnly = true)
    override fun getCredentialsByEmailOrNull(email: Email): AuthWithCredentials? =
        authRepo.getCredentialsByEmailOrNull(email)

    @Transactional(readOnly = true)
    override fun getByCredentialsOrNull(email: Email, password: String): Auth? =
        authRepo.getCredentialsByEmailOrNull(email)?.takeIf { passwordEncoder.matches(password, it.passwordHash) }

    @Transactional(readOnly = true)
    override fun getByCredentials(email: Email, password: String): Auth =
        getByCredentialsOrNull(email, password) ?: throw InvalidCredentialsException()

    @Transactional
    override fun assignEmail(userId: UUID, email: Email) {
        authRepo.updateEmailByUser(userId, email)
    }

    override fun generatePasswordHash(password: NewPassword): String =
        passwordEncoder.encode(password)
}
