package com.goodchoice.domain.common

import com.goodchoice.domain.auth.model.UserRole
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(INTERNAL_SERVER_ERROR)
abstract class ApplicationException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

@ResponseStatus(BAD_REQUEST)
abstract class FormatException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class StringMaxLengthException(val string: String, val maxLength: Int) : FormatException()
class StringSingleLineException(val string: String) : FormatException()
class StringTrimException(val string: String) : FormatException()
class StringNormalizationException(val string: String) : FormatException()
class InvalidEmailException(val email: String) : FormatException()
class PasswordMinLengthException(val minLength: Int) : FormatException()

@ResponseStatus(FORBIDDEN)
abstract class AuthenticationException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class UserRoleRequiredException(val actual: UserRole?, val expected: UserRole) : AuthenticationException()
