package com.goodchoice.domain.auth

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
abstract class AuthenticationException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class AuthenticationRequiredException : AuthenticationException()


@ResponseStatus(HttpStatus.FORBIDDEN)
abstract class AuthorizationException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class UserRoleRequiredException(val actual: UserRole?, val expected: UserRole) : AuthorizationException()
class ConfirmedEmailRequiredException : AuthorizationException()
