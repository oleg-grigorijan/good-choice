package com.goodchoice.domain.common

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(INTERNAL_SERVER_ERROR)
abstract class ApplicationException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)


@ResponseStatus(BAD_REQUEST)
abstract class FormatException(
    message: String? = null,
    cause: Throwable? = null,
) : ApplicationException(message, cause)

class StringMaxLengthException(val string: String, val maxLength: Int) : FormatException()
class StringSingleLineException(val string: String) : FormatException()
class StringTrimException(val string: String) : FormatException()
class StringNormalizationException(val string: String) : FormatException()

class InvalidEmailException(val email: String) : FormatException()

class PasswordMinLengthException(val minLength: Int) : FormatException()

class PageOffsetNegativeException : FormatException()
class PageLimitNegativeException : FormatException()

class UnexpectedUserRoleException : FormatException()
