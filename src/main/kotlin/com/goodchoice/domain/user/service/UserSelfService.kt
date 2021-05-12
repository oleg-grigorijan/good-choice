package com.goodchoice.domain.user.service

import com.goodchoice.domain.user.UserIncorrectCredentialsException
import com.goodchoice.domain.user.model.UserLoginRequest
import com.goodchoice.domain.user.model.UserRole
import com.goodchoice.domain.user.persistence.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder

interface UserSelfService {
    fun login(userLoginRequest: UserLoginRequest): UserRole
}

class UserSelfServiceImpl(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) :
    UserSelfService {
    override fun login(userLoginRequest: UserLoginRequest): UserRole =
        userRepository.getByEmailOrNull(userLoginRequest.email)?.let {
            if (passwordEncoder.matches(userLoginRequest.password.value, it.passwordHash)) {
                UserRole(it.role)
            } else {
                null
            }
        } ?: throw UserIncorrectCredentialsException()

}