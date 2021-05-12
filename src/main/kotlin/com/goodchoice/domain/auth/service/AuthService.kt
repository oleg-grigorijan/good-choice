package com.goodchoice.domain.auth.service

import com.goodchoice.domain.auth.AuthenticationRequiredException
import com.goodchoice.domain.auth.InvalidCredentialsException
import com.goodchoice.domain.auth.model.Auth
import com.goodchoice.domain.auth.model.AuthWithCredentials
import com.goodchoice.domain.auth.persistence.AuthRepository
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.RawPassword
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
    fun getByCredentialsOrNull(email: Email, password: RawPassword): Auth?
    fun getByCredentials(email: Email, password: RawPassword): Auth
    fun assignEmail(userId: UUID, email: Email)
    fun generatePasswordHash(password: RawPassword): String
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
    override fun getByCredentialsOrNull(email: Email, password: RawPassword): Auth? =
        authRepo.getCredentialsByEmailOrNull(email)?.let {
            if (passwordEncoder.matches(password.value, it.passwordHash)) {
                it
            } else {
                null
            }
        }

    @Transactional(readOnly = true)
    override fun getByCredentials(email: Email, password: RawPassword): Auth =
        authRepo.getCredentialsByEmailOrNull(email)?.let {
            if (passwordEncoder.matches(password.value, it.passwordHash)) {
                it
            } else {
                null
            }
        } ?: throw InvalidCredentialsException()

    @Transactional
    override fun assignEmail(userId: UUID, email: Email) {
        authRepo.updateEmailByUser(userId, email)
    }

    override fun generatePasswordHash(password: RawPassword): String =
        passwordEncoder.encode(password)
}
