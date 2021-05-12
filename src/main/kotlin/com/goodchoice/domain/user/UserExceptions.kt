package com.goodchoice.domain.user

import com.goodchoice.domain.common.ApplicationException
import com.goodchoice.domain.common.model.Email
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

abstract class UserException(
    message: String? = null,
    cause: Throwable? = null,
) : ApplicationException(message, cause)

@ResponseStatus(CONFLICT)
class UserExistsByEmailException(val email: Email) : UserException()

abstract class EmployeeException(
    message: String? = null,
    cause: Throwable? = null,
) : UserException(message, cause)


@ResponseStatus(NOT_FOUND)
class EmployeeInvitationNotFoundException : EmployeeException()

@ResponseStatus(CONFLICT)
class EmployeeInvitationExpiredException : EmployeeException()
