package com.goodchoice.domain.common

import com.goodchoice.domain.auth.model.UserRole

abstract class ApplicationException(
    val messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

abstract class FormatException(
    messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(messageKey, message, cause)

class StringMaxLengthException(val actual: Int, val max: Int) : FormatException("")
class StringSingleLineException : FormatException("")
class StringTrimException : FormatException("")
class StringNormalizationException : FormatException("")
class InvalidEmailException : FormatException("")
class WeakPasswordException : FormatException("")

abstract class AuthenticationException(
    messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(messageKey, message, cause)

class UserRoleRequiredException(val actual: UserRole?, val expected: UserRole) : AuthenticationException("")
